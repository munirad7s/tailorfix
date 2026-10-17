import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { useApi } from '../useApi'
import { ALTERATION_LABELS, REASON_LABELS, formatMoney, type ReturnIntent } from '../types'

const REJECTION_TEXT: Record<string, string> = {
  category_not_alterable: 'Diese Warengruppe lässt sich nicht ändern.',
  item_price_below_minimum: 'Bei diesem Artikelpreis lohnt sich eine Änderung nicht.',
  reason_not_fixable_by_tailor: 'Dieses Problem kann eine Schneiderei nicht lösen.',
  voucher_would_exceed_saving: 'Eine Änderung wäre teurer als die Rücksendung.',
}

export default function OfferPage() {
  const { api } = useApi()
  const { intentId } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const [intent, setIntent] = useState<ReturnIntent | null>((location.state as ReturnIntent | null) ?? null)
  const [tailorId, setTailorId] = useState<string | null>(null)
  const [standardReturn, setStandardReturn] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (intent || !api || !intentId) return
    api.getReturnIntent(intentId).then(setIntent).catch((e: Error) => setError(e.message))
  }, [api, intent, intentId])

  useEffect(() => {
    const first = intent?.offer?.nearby_tailors[0]
    if (first && !tailorId) setTailorId(first.id)
  }, [intent, tailorId])

  if (error) return <p className="error">{error}</p>
  if (!intent) return <p className="muted">Prüfe Möglichkeiten …</p>

  if (standardReturn || !intent.eligible || !intent.offer) {
    return (
      <>
        <h1>Rücksendung vorbereitet</h1>
        <div className="card">
          <p>
            <strong>{intent.order_item.brand}</strong> · {intent.order_item.title}
            <br />
            Grund: {REASON_LABELS[intent.reason]}
          </p>
          {!intent.eligible && intent.rejection_reason && (
            <p className="muted small">Kein Tailor Fix möglich: {REJECTION_TEXT[intent.rejection_reason] ?? intent.rejection_reason}</p>
          )}
          <p className="muted small">Hier folgt im echten Flow das Retourenlabel. Im Prototyp endet der Standardweg an dieser Stelle.</p>
        </div>
        <Link className="btn secondary block" to="/">Zurück zu meinen Bestellungen</Link>
      </>
    )
  }

  const offer = intent.offer
  const tailors = offer.nearby_tailors

  async function claim() {
    if (!api || !tailorId || !offer) return
    setBusy(true)
    setError(null)
    try {
      const voucher = await api.createVoucher(offer.id, tailorId)
      navigate(`/voucher/${voucher.code}`, { state: voucher })
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <div className="hero">
        <div className="eyebrow">Neu · Tailor Fix</div>
        <h1>Behalten und vor Ort anpassen lassen. Kostenlos.</h1>
        <p>
          Statt zurückzuschicken: Eine zertifizierte Schneiderei in deiner Nähe übernimmt{' '}
          <strong>{ALTERATION_LABELS[offer.alteration_type].toLowerCase()}</strong>. Zalando und die Marke zahlen bis zu{' '}
          <strong>{formatMoney(offer.voucher_value)}</strong>.
        </p>
        <div className="chips">
          <span className="chip">Kein Paket, kein Warten</span>
          <span className="chip">ca. {offer.estimated_co2_saved_grams} g CO₂ gespart</span>
          {offer.requires_tailor_check && <span className="chip warn">Schneiderei prüft Machbarkeit</span>}
        </div>
      </div>

      <h2>Schneiderei wählen</h2>
      {tailors.length === 0 && <p className="muted">Keine Partner-Schneiderei im Umkreis von 5 km. Standardrücksendung möglich.</p>}
      <ul className="list">
        {tailors.map((t) => (
          <li key={t.id}>
            <label className={`option tailor ${tailorId === t.id ? 'selected' : ''}`}>
              <input type="radio" name="tailor" value={t.id} checked={tailorId === t.id} onChange={() => setTailorId(t.id)} />
              <span className="grow">
                <span className="brand">{t.name}</span>
                <span className="small muted">
                  {t.street}, {t.postal_code} {t.city}
                  {t.distance_km != null && <> · {t.distance_km.toLocaleString('de-DE')} km</>}
                </span>
                <span className="small muted">Fertig in ca. {t.turnaround_days} Tagen{t.opening_hours ? ` · ${t.opening_hours}` : ''}</span>
              </span>
            </label>
          </li>
        ))}
      </ul>

      {error && <p className="error">{error}</p>}
      <button className="btn primary block" disabled={!tailorId || busy || tailors.length === 0} onClick={claim}>
        {busy ? 'Einen Moment …' : `Gutschein über ${formatMoney(offer.voucher_value)} holen`}
      </button>
      <button className="btn link block" onClick={() => setStandardReturn(true)}>
        Trotzdem zurücksenden
      </button>

      <details className="card small">
        <summary>Warum Zalando das anbietet (Business-Case-Sicht)</summary>
        <p>
          Eine Rücksendung kostet die Plattform im Schnitt {formatMoney(offer.estimated_return_cost)} für Logistik und Bearbeitung,
          und der Umsatz geht verloren. Mit dem Gutschein bleibt der Kauf bestehen. Geschätzte Ersparnis gegenüber der Retoure:{' '}
          <strong>{formatMoney(offer.estimated_saving)}</strong>. Annahmen und Quellen stehen in docs/02-business-case.md.
        </p>
      </details>
    </>
  )
}
