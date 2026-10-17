import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import QRCode from 'qrcode'
import { useApi } from '../useApi'
import { ALTERATION_LABELS, formatMoney, type Voucher } from '../types'

export default function VoucherPage() {
  const { api } = useApi()
  const { code } = useParams()
  const location = useLocation()
  const [voucher, setVoucher] = useState<Voucher | null>((location.state as Voucher | null) ?? null)
  const [qr, setQr] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (voucher || !api || !code) return
    api.getVoucher(code).then(setVoucher).catch((e: Error) => setError(e.message))
  }, [api, voucher, code])

  useEffect(() => {
    if (!voucher) return
    QRCode.toDataURL(voucher.qr_payload, { width: 220, margin: 1 }).then(setQr)
  }, [voucher])

  if (error) return <p className="error">{error}</p>
  if (!voucher) return <p className="muted">Lade Gutschein …</p>

  const t = voucher.tailor

  return (
    <>
      <h1>Dein Tailor-Fix-Gutschein</h1>
      <div className="card center">
        {qr && <img src={qr} alt={`QR-Code ${voucher.code}`} className="qr" />}
        <div className="code">{voucher.code}</div>
        <div className="muted small">
          {ALTERATION_LABELS[voucher.alteration_type]} · Wert {formatMoney(voucher.value)} · gültig bis{' '}
          {new Date(voucher.expires_at).toLocaleDateString('de-DE')}
        </div>
        <div className={`status ${voucher.status.toLowerCase()}`}>{voucher.status === 'ISSUED' ? 'Bereit zum Einlösen' : voucher.status === 'REDEEMED' ? 'Eingelöst' : 'Abgelaufen'}</div>
      </div>

      <h2>So geht es weiter</h2>
      <ol className="steps">
        <li>
          Bring den Artikel zu <strong>{t.name}</strong>, {t.street}, {t.postal_code} {t.city}
          {t.opening_hours ? ` (${t.opening_hours})` : ''}.
        </li>
        <li>Zeig diesen QR-Code. Die Schneiderei rechnet bis {formatMoney(voucher.value)} direkt mit Zalando ab. Du zahlst nichts.</li>
        <li>Abholung in ca. {t.turnaround_days} Tagen. {voucher.requires_tailor_check ? 'Die Schneiderei prüft vorher, ob die Änderung möglich ist; sonst bleibt die Rücksendung offen.' : ''}</li>
      </ol>
      <p className="muted small">Hinter den Kulissen: Marke trägt {formatMoney(voucher.brand_share)}, Zalando den Rest.</p>
      <Link className="btn secondary block" to="/">Fertig</Link>
    </>
  )
}
