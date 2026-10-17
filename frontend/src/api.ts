import type { Location, OrderItem, Problem, Redemption, ReturnIntent, ReturnReason, Voucher } from './types'
import { mockApi } from './mock'

export interface TailorFixApi {
  listOrderItems(): Promise<OrderItem[]>
  createReturnIntent(orderItemId: string, reason: ReturnReason, location: Location): Promise<ReturnIntent>
  getReturnIntent(id: string): Promise<ReturnIntent>
  createVoucher(offerId: string, tailorId: string): Promise<Voucher>
  getVoucher(code: string): Promise<Voucher>
  redeemVoucher(code: string): Promise<Redemption>
}

export class ApiError extends Error {
  constructor(public readonly status: number, public readonly problem: Problem) {
    super(problem.detail ?? problem.title ?? `HTTP ${status}`)
  }
}

const API_BASE: string = import.meta.env.VITE_API_BASE ?? '/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(API_BASE + path, {
    ...init,
    headers: { 'Content-Type': 'application/json', Accept: 'application/json, application/problem+json', ...(init?.headers ?? {}) },
  })
  if (!response.ok) {
    const problem = (await response.json().catch(() => ({}))) as Problem
    throw new ApiError(response.status, problem)
  }
  return (await response.json()) as T
}

const httpApi: TailorFixApi = {
  listOrderItems: () => request('/order-items'),
  createReturnIntent: (orderItemId, reason, location) =>
    request('/return-intents', { method: 'POST', body: JSON.stringify({ order_item_id: orderItemId, reason, location }) }),
  getReturnIntent: (id) => request(`/return-intents/${id}`),
  createVoucher: (offerId, tailorId) =>
    request('/vouchers', { method: 'POST', body: JSON.stringify({ offer_id: offerId, tailor_id: tailorId }) }),
  getVoucher: (code) => request(`/vouchers/${encodeURIComponent(code)}`),
  redeemVoucher: (code) => request(`/vouchers/${encodeURIComponent(code)}/redemptions`, { method: 'POST' }),
}

export type ApiMode = 'backend' | 'mock'

let resolved: { api: TailorFixApi; mode: ApiMode } | null = null

/**
 * Uses the Kotlin backend when it answers, otherwise the in-browser mock so that the click-dummy
 * works for teammates without a JDK or Docker (course sessions 7 and 8).
 */
export async function resolveApi(): Promise<{ api: TailorFixApi; mode: ApiMode }> {
  if (resolved) return resolved
  if (import.meta.env.VITE_USE_MOCK === 'true') {
    resolved = { api: mockApi, mode: 'mock' }
    return resolved
  }
  try {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), 1500)
    const response = await fetch(API_BASE + '/order-items', { signal: controller.signal })
    clearTimeout(timer)
    resolved = response.ok ? { api: httpApi, mode: 'backend' } : { api: mockApi, mode: 'mock' }
  } catch {
    resolved = { api: mockApi, mode: 'mock' }
  }
  return resolved
}

export const DEMO_LOCATIONS: Record<string, { label: string; location: Location }> = {
  nuernberg: { label: 'Nürnberg Innenstadt (Lange Gasse)', location: { lat: 49.4525, lon: 11.0872 } },
  erlangen: { label: 'Erlangen Innenstadt (Hugenottenplatz)', location: { lat: 49.5972, lon: 11.0041 } },
  suedgelaende: { label: 'Erlangen Südgelände (FAU Tech)', location: { lat: 49.5735, lon: 11.0281 } },
}

export function currentLocationKey(): string {
  try {
    return localStorage.getItem('tailorfix.location') ?? 'nuernberg'
  } catch {
    return 'nuernberg'
  }
}

export function setLocationKey(key: string) {
  try {
    localStorage.setItem('tailorfix.location', key)
  } catch {
    // private mode: ignore
  }
}
