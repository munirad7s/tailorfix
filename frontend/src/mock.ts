import type { TailorFixApi } from './api'
import { ApiError } from './api'
import type { AlterationType, Category, Location, OrderItem, Redemption, ReturnIntent, ReturnReason, Tailor, Voucher } from './types'

// A deliberately small copy of the backend rules so the click-dummy behaves like the real service.
// Source of truth stays backend/src/main/kotlin/de/tailorfix/domain/EligibilityPolicy.kt.

const RETURN_COST = 11
const MIN_PRICE = 50
const MAX_VOUCHER_SHARE = 0.35
const RETAINED_MARGIN = 0.4

const orderItems: OrderItem[] = [
  { id: 'i1', order_number: '10512-7788-2041', brand: 'Selected Homme', title: 'Slim Fit Anzughose, dunkelblau', category: 'TROUSERS', size: '50', price: { amount: 89.95, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
  { id: 'i2', order_number: '10512-7788-2041', brand: 'Hugo', title: 'Slim Fit Sakko, dunkelblau', category: 'BLAZERS', size: '50', price: { amount: 249.95, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
  { id: 'i3', order_number: '10512-7788-2041', brand: 'Vero Moda', title: 'Cocktailkleid, smaragd', category: 'DRESSES', size: '38', price: { amount: 79.99, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
  { id: 'i4', order_number: '10512-7788-2041', brand: 'Olymp', title: 'Businesshemd Level Five, weiß', category: 'SHIRTS', size: '40', price: { amount: 59.95, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
  { id: 'i5', order_number: '10512-7788-2041', brand: 'Nike', title: 'Air Max, weiß', category: 'SHOES', size: '43', price: { amount: 129.99, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
  { id: 'i6', order_number: '10512-7788-2041', brand: 'Pier One', title: 'T-Shirt Basic, 3er-Pack', category: 'SPORTSWEAR', size: 'M', price: { amount: 19.99, currency: 'EUR' }, delivered_at: '2026-10-12T09:30:00Z' },
]

const ALL: AlterationType[] = ['HEM_SHORTEN', 'WAIST_TAKE_IN', 'SEAM_LET_OUT', 'SLEEVE_SHORTEN', 'GENERAL_FITTING']

const tailors: Tailor[] = [
  { id: 't1', name: 'Änderungsschneiderei am Hauptmarkt', street: 'Waaggasse 4', postal_code: '90403', city: 'Nürnberg', lat: 49.4537, lon: 11.0769, services: ALL, certified: true, turnaround_days: 3, opening_hours: 'Mo–Fr 9–18, Sa 10–14' },
  { id: 't2', name: 'Atelier Lorenz Maßschneiderei', street: 'Königstraße 32', postal_code: '90402', city: 'Nürnberg', lat: 49.451, lon: 11.079, services: ALL, certified: true, turnaround_days: 5, opening_hours: 'Mo–Fr 10–18' },
  { id: 't3', name: 'Nadelwerk Gostenhof', street: 'Gostenhofer Hauptstraße 21', postal_code: '90443', city: 'Nürnberg', lat: 49.4498, lon: 11.0605, services: ['HEM_SHORTEN', 'WAIST_TAKE_IN', 'SLEEVE_SHORTEN'], certified: true, turnaround_days: 2, opening_hours: 'Mo–Sa 9–19' },
  { id: 't4', name: 'Schneiderei Südstadt', street: 'Wölckernstraße 18', postal_code: '90459', city: 'Nürnberg', lat: 49.4423, lon: 11.0788, services: ['HEM_SHORTEN', 'WAIST_TAKE_IN', 'GENERAL_FITTING'], certified: true, turnaround_days: 4, opening_hours: 'Mo–Fr 9–17' },
  { id: 't11', name: 'Änderungsschneiderei Hugenottenplatz', street: 'Hauptstraße 55', postal_code: '91054', city: 'Erlangen', lat: 49.5973, lon: 11.0043, services: ALL, certified: true, turnaround_days: 3, opening_hours: 'Mo–Fr 9–18, Sa 9–13' },
  { id: 't12', name: 'Schneiderstube an den Arcaden', street: 'Nürnberger Straße 9', postal_code: '91052', city: 'Erlangen', lat: 49.5951, lon: 11.0031, services: ['HEM_SHORTEN', 'WAIST_TAKE_IN', 'SLEEVE_SHORTEN'], certified: true, turnaround_days: 2, opening_hours: 'Mo–Sa 10–20' },
  { id: 't13', name: 'Maßschneiderei Burgberg', street: 'Burgbergstraße 40', postal_code: '91054', city: 'Erlangen', lat: 49.6035, lon: 11.0085, services: ALL, certified: true, turnaround_days: 5, opening_hours: 'Mo–Fr 10–18' },
  { id: 't15', name: 'Schneiderei Tennenlohe', street: 'Sebastianstraße 14', postal_code: '91058', city: 'Erlangen', lat: 49.5521, lon: 11.0297, services: ['HEM_SHORTEN', 'WAIST_TAKE_IN', 'SLEEVE_SHORTEN', 'GENERAL_FITTING'], certified: true, turnaround_days: 4, opening_hours: 'Mo–Fr 9–18' },
]

const intents = new Map<string, ReturnIntent>()
const vouchers = new Map<string, Voucher>()
const issuedOffers = new Set<string>()

const ALTERABLE: Category[] = ['TROUSERS', 'SUITS', 'BLAZERS', 'DRESSES', 'SHIRTS', 'COATS', 'SKIRTS']
const HEMMABLE: Category[] = ['TROUSERS', 'DRESSES', 'SKIRTS', 'COATS', 'SUITS']
const LET_OUT: Category[] = ['TROUSERS', 'SUITS', 'BLAZERS', 'DRESSES', 'SKIRTS']
const SLEEVED: Category[] = ['SHIRTS', 'BLAZERS', 'SUITS', 'COATS', 'DRESSES']

function alterationFor(reason: ReturnReason, category: Category): AlterationType | null {
  switch (reason) {
    case 'TOO_LONG': return HEMMABLE.includes(category) ? 'HEM_SHORTEN' : null
    case 'TOO_WIDE': return 'WAIST_TAKE_IN'
    case 'TOO_TIGHT': return LET_OUT.includes(category) ? 'SEAM_LET_OUT' : null
    case 'SLEEVES_TOO_LONG': return SLEEVED.includes(category) ? 'SLEEVE_SHORTEN' : null
    case 'DOES_NOT_FIT_OTHER': return 'GENERAL_FITTING'
    default: return null
  }
}

function listPrice(alteration: AlterationType, category: Category): number {
  switch (alteration) {
    case 'HEM_SHORTEN': return category === 'COATS' ? 25 : 15
    case 'WAIST_TAKE_IN': return category === 'TROUSERS' || category === 'SKIRTS' ? 20 : category === 'SHIRTS' ? 25 : 35
    case 'SEAM_LET_OUT': return 25
    case 'SLEEVE_SHORTEN': return category === 'SHIRTS' ? 25 : 45
    case 'GENERAL_FITTING': return 25
  }
}

function distanceKm(a: Location, b: Location): number {
  const toRad = (d: number) => (d * Math.PI) / 180
  const dLat = toRad(b.lat - a.lat)
  const dLon = toRad(b.lon - a.lon)
  const h = Math.sin(dLat / 2) ** 2 + Math.cos(toRad(a.lat)) * Math.cos(toRad(b.lat)) * Math.sin(dLon / 2) ** 2
  return 2 * 6371.0088 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h))
}

function uuid(): string {
  return crypto.randomUUID()
}

function code(): string {
  const alphabet = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  const pick = () => alphabet[Math.floor(Math.random() * alphabet.length)]
  return `${pick()}${pick()}${pick()}${pick()}-${pick()}${pick()}${pick()}${pick()}`
}

const round2 = (n: number) => Math.round(n * 100) / 100
const delay = (ms = 250) => new Promise((r) => setTimeout(r, ms))

export const mockApi: TailorFixApi = {
  async listOrderItems() {
    await delay()
    return orderItems
  },

  async createReturnIntent(orderItemId, reason, location) {
    await delay()
    const item = orderItems.find((i) => i.id === orderItemId)
    if (!item) throw new ApiError(404, { title: 'Resource not found', detail: `order_item ${orderItemId} not found` })

    const id = uuid()
    const base: ReturnIntent = { id, order_item: item, reason, eligible: false, created_at: new Date().toISOString() }

    let rejection: string | null = null
    const alteration = alterationFor(reason, item.category)
    if (!ALTERABLE.includes(item.category)) rejection = 'category_not_alterable'
    else if (item.price.amount < MIN_PRICE) rejection = 'item_price_below_minimum'
    else if (!alteration) rejection = 'reason_not_fixable_by_tailor'

    if (rejection || !alteration) {
      const intent = { ...base, rejection_reason: rejection }
      intents.set(id, intent)
      return intent
    }

    const voucherValue = Math.min(listPrice(alteration, item.category), round2(item.price.amount * MAX_VOUCHER_SHARE))
    const saving = RETURN_COST + item.price.amount * RETAINED_MARGIN
    if (voucherValue >= saving) {
      const intent = { ...base, rejection_reason: 'voucher_would_exceed_saving' }
      intents.set(id, intent)
      return intent
    }

    const nearby = tailors
      .filter((t) => t.certified && t.services.includes(alteration))
      .map((t) => ({ ...t, distance_km: Math.round(distanceKm(location, t) * 10) / 10 }))
      .filter((t) => t.distance_km <= 5)
      .sort((a, b) => a.distance_km - b.distance_km)
      .slice(0, 5)

    const intent: ReturnIntent = {
      ...base,
      eligible: true,
      offer: {
        id: uuid(),
        alteration_type: alteration,
        voucher_value: { amount: voucherValue, currency: 'EUR' },
        estimated_return_cost: { amount: RETURN_COST, currency: 'EUR' },
        estimated_saving: { amount: round2(saving - voucherValue), currency: 'EUR' },
        estimated_co2_saved_grams: 500,
        requires_tailor_check: alteration === 'SEAM_LET_OUT' || alteration === 'GENERAL_FITTING',
        expires_at: new Date(Date.now() + 14 * 86400000).toISOString(),
        nearby_tailors: nearby,
      },
    }
    intents.set(id, intent)
    return intent
  },

  async getReturnIntent(id) {
    await delay(100)
    const intent = intents.get(id)
    if (!intent) throw new ApiError(404, { title: 'Resource not found', detail: `return_intent ${id} not found` })
    return intent
  },

  async createVoucher(offerId, tailorId) {
    await delay()
    const intent = [...intents.values()].find((i) => i.offer?.id === offerId)
    const tailor = tailors.find((t) => t.id === tailorId)
    if (!intent?.offer || !tailor) throw new ApiError(404, { title: 'Resource not found', detail: 'offer or tailor not found' })
    if (issuedOffers.has(offerId)) throw new ApiError(409, { title: 'Conflict', detail: 'voucher already issued for offer' })
    issuedOffers.add(offerId)
    const voucher: Voucher = {
      code: code(),
      status: 'ISSUED',
      value: intent.offer.voucher_value,
      brand_share: { amount: round2(intent.offer.voucher_value.amount / 2), currency: 'EUR' },
      alteration_type: intent.offer.alteration_type,
      requires_tailor_check: intent.offer.requires_tailor_check,
      tailor,
      qr_payload: '',
      issued_at: new Date().toISOString(),
      expires_at: intent.offer.expires_at,
      redeemed_at: null,
    }
    voucher.qr_payload = `tailorfix:voucher:${voucher.code}`
    vouchers.set(voucher.code, voucher)
    return voucher
  },

  async getVoucher(codeValue) {
    await delay(100)
    const voucher = vouchers.get(codeValue.toUpperCase())
    if (!voucher) throw new ApiError(404, { title: 'Resource not found', detail: `voucher ${codeValue} not found` })
    return voucher
  },

  async redeemVoucher(codeValue) {
    await delay()
    const voucher = vouchers.get(codeValue.toUpperCase())
    if (!voucher) throw new ApiError(404, { title: 'Resource not found', detail: `voucher ${codeValue} not found` })
    if (voucher.status === 'REDEEMED') throw new ApiError(409, { title: 'Conflict', detail: 'voucher already redeemed' })
    voucher.status = 'REDEEMED'
    voucher.redeemed_at = new Date().toISOString()
    const redemption: Redemption = { voucher_code: voucher.code, redeemed_at: voucher.redeemed_at, payout_to_tailor: voucher.value, voucher }
    return redemption
  },
}
