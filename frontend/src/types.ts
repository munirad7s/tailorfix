// Mirrors api/openapi.yaml (snake_case on the wire, as the Zalando API guidelines require).

export type Category =
  | 'TROUSERS' | 'SUITS' | 'BLAZERS' | 'DRESSES' | 'SHIRTS' | 'COATS' | 'SKIRTS'
  | 'KNITWEAR' | 'SHOES' | 'ACCESSORIES' | 'SPORTSWEAR'

export type ReturnReason =
  | 'TOO_LONG' | 'TOO_SHORT' | 'TOO_WIDE' | 'TOO_TIGHT' | 'SLEEVES_TOO_LONG'
  | 'DOES_NOT_FIT_OTHER' | 'DO_NOT_LIKE' | 'DEFECTIVE' | 'WRONG_ITEM' | 'ORDERED_SEVERAL_SIZES'

export type AlterationType = 'HEM_SHORTEN' | 'WAIST_TAKE_IN' | 'SEAM_LET_OUT' | 'SLEEVE_SHORTEN' | 'GENERAL_FITTING'

export type VoucherStatus = 'ISSUED' | 'REDEEMED' | 'EXPIRED'

export interface Money { amount: number; currency: string }

export interface Location { lat: number; lon: number }

export interface OrderItem {
  id: string
  order_number: string
  brand: string
  title: string
  category: Category
  size: string
  price: Money
  image_url?: string | null
  delivered_at: string
}

export interface Tailor {
  id: string
  name: string
  street: string
  postal_code: string
  city: string
  lat: number
  lon: number
  services: AlterationType[]
  certified: boolean
  turnaround_days: number
  opening_hours?: string | null
  website?: string | null
  distance_km?: number | null
}

export interface TailorFixOffer {
  id: string
  alteration_type: AlterationType
  voucher_value: Money
  estimated_return_cost: Money
  estimated_saving: Money
  estimated_co2_saved_grams: number
  requires_tailor_check: boolean
  expires_at: string
  nearby_tailors: Tailor[]
}

export interface ReturnIntent {
  id: string
  order_item: OrderItem
  reason: ReturnReason
  eligible: boolean
  rejection_reason?: string | null
  offer?: TailorFixOffer | null
  created_at: string
}

export interface Voucher {
  code: string
  status: VoucherStatus
  value: Money
  brand_share: Money
  alteration_type: AlterationType
  requires_tailor_check: boolean
  tailor: Tailor
  qr_payload: string
  issued_at: string
  expires_at: string
  redeemed_at?: string | null
}

export interface Redemption {
  voucher_code: string
  redeemed_at: string
  payout_to_tailor: Money
  voucher: Voucher
}

export interface Problem {
  type?: string
  title?: string
  status?: number
  detail?: string
}

export const REASON_LABELS: Record<ReturnReason, string> = {
  TOO_LONG: 'Zu lang',
  TOO_SHORT: 'Zu kurz',
  TOO_WIDE: 'Zu weit',
  TOO_TIGHT: 'Zu eng',
  SLEEVES_TOO_LONG: 'Ärmel zu lang',
  DOES_NOT_FIT_OTHER: 'Passt nicht (sonstiges)',
  DO_NOT_LIKE: 'Gefällt mir nicht',
  DEFECTIVE: 'Beschädigt / defekt',
  WRONG_ITEM: 'Falscher Artikel geliefert',
  ORDERED_SEVERAL_SIZES: 'Mehrere Größen bestellt',
}

export const ALTERATION_LABELS: Record<AlterationType, string> = {
  HEM_SHORTEN: 'Saum kürzen',
  WAIST_TAKE_IN: 'Taille / Bund enger machen',
  SEAM_LET_OUT: 'Nähte auslassen',
  SLEEVE_SHORTEN: 'Ärmel kürzen',
  GENERAL_FITTING: 'Anprobe und Anpassung',
}

export const CATEGORY_LABELS: Record<Category, string> = {
  TROUSERS: 'Hose',
  SUITS: 'Anzug',
  BLAZERS: 'Sakko',
  DRESSES: 'Kleid',
  SHIRTS: 'Hemd',
  COATS: 'Mantel',
  SKIRTS: 'Rock',
  KNITWEAR: 'Strick',
  SHOES: 'Schuhe',
  ACCESSORIES: 'Accessoire',
  SPORTSWEAR: 'Sportbekleidung',
}

export function formatMoney(m: Money): string {
  return new Intl.NumberFormat('de-DE', { style: 'currency', currency: m.currency }).format(m.amount)
}
