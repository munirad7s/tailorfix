import { useState } from 'react'
import { useApi } from '../useApi'
import { ALTERATION_LABELS, formatMoney, type Redemption, type Voucher } from '../types'

export default function TailorPortalPage() {
  const { api } = useApi()
  const [code, setCode] = useState('')
  const [voucher, setVoucher] = useState<Voucher | null>(null)
  const [redemption, setRedemption] = useState<Redemption | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  async function lookup() {
    if (!api) return
    setBusy(true)
    setError(null)
    setRedemption(null)
    try {
      setVoucher(await api.getVoucher(code.trim().toUpperCase()))
    } catch (e) {
      setVoucher(null)
      setError((e as Error).message)
    } finally {
      setBusy(false)
    }
  }

  async function redeem() {
    if (!api || !voucher) return
    setBusy(true)
    setError(null)
    try {
      const result = await api.redeemVoucher(voucher.code)
      setRedemption(result)
      setVoucher(result.voucher)
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <h1>Schneiderei-Portal</h1>
      <p className="muted small">Partneransicht: Gutschein prüfen und einlösen. Die Auszahlung läuft über die Zalando-Partnerabrechnung.</p>
      <div className="row">
        <input
          className="input"
          placeholder="Code, z. B. K7PM-4Q2X"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && lookup()}
        />
        <button className="btn secondary" disabled={!code || busy} onClick={lookup}>Prüfen</button>
      </div>
      {error && <p className="error">{error}</p>}

      {voucher && (
        <div className="card">
          <div className="brand">{voucher.code}</div>
          <div>{ALTERATION_LABELS[voucher.alteration_type]} · bis {formatMoney(voucher.value)}</div>
          <div className="muted small">
            Schneiderei: {voucher.tailor.name} · gültig bis {new Date(voucher.expires_at).toLocaleDateString('de-DE')}
          </div>
          {voucher.requires_tailor_check && <p className="small warn-text">Machbarkeit bitte vor der Annahme prüfen.</p>}
          <div className={`status ${voucher.status.toLowerCase()}`}>{voucher.status}</div>
          {voucher.status === 'ISSUED' && (
            <button className="btn primary block" disabled={busy} onClick={redeem}>Einlösen und Auszahlung anfordern</button>
          )}
          {redemption && (
            <p className="success">
              Eingelöst am {new Date(redemption.redeemed_at).toLocaleString('de-DE')}. Auszahlung {formatMoney(redemption.payout_to_tailor)} angewiesen.
            </p>
          )}
        </div>
      )}
    </>
  )
}
