import { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { DEMO_LOCATIONS, currentLocationKey, setLocationKey } from '../api'
import { useApi } from '../useApi'
import { REASON_LABELS, formatMoney, type OrderItem, type ReturnReason } from '../types'

const REASONS = Object.keys(REASON_LABELS) as ReturnReason[]

export default function ReturnReasonPage() {
  const { api } = useApi()
  const { itemId } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const [item, setItem] = useState<OrderItem | null>((location.state as OrderItem | null) ?? null)
  const [reason, setReason] = useState<ReturnReason | null>(null)
  const [locationKey, setKey] = useState(currentLocationKey())
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (item || !api || !itemId) return
    api.listOrderItems().then((items) => setItem(items.find((i) => i.id === itemId) ?? null))
  }, [api, item, itemId])

  if (!item) return <p className="muted">Lade Artikel …</p>

  async function submit() {
    if (!api || !reason || !item) return
    setBusy(true)
    setError(null)
    try {
      setLocationKey(locationKey)
      const intent = await api.createReturnIntent(item.id, reason, DEMO_LOCATIONS[locationKey].location)
      navigate(`/offer/${intent.id}`, { state: intent })
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <h1>Artikel zurückgeben</h1>
      <div className="card item">
        <div className="grow">
          <div className="brand">{item.brand}</div>
          <div>{item.title}</div>
          <div className="muted small">Größe {item.size} · {formatMoney(item.price)}</div>
        </div>
      </div>

      <h2>Warum möchtest du den Artikel zurückgeben?</h2>
      <ul className="list">
        {REASONS.map((r) => (
          <li key={r}>
            <label className={`option ${reason === r ? 'selected' : ''}`}>
              <input type="radio" name="reason" value={r} checked={reason === r} onChange={() => setReason(r)} />
              <span>{REASON_LABELS[r]}</span>
            </label>
          </li>
        ))}
      </ul>

      <label className="field">
        <span className="small muted">Lieferadresse (Demo)</span>
        <select value={locationKey} onChange={(e) => setKey(e.target.value)}>
          {Object.entries(DEMO_LOCATIONS).map(([key, value]) => (
            <option key={key} value={key}>{value.label}</option>
          ))}
        </select>
      </label>

      {error && <p className="error">{error}</p>}
      <button className="btn primary block" disabled={!reason || busy} onClick={submit}>
        {busy ? 'Einen Moment …' : 'Weiter'}
      </button>
    </>
  )
}
