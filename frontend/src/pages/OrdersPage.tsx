import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useApi } from '../useApi'
import { CATEGORY_LABELS, formatMoney, type OrderItem } from '../types'

export default function OrdersPage() {
  const { api } = useApi()
  const navigate = useNavigate()
  const [items, setItems] = useState<OrderItem[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!api) return
    api.listOrderItems().then(setItems).catch((e: Error) => setError(e.message))
  }, [api])

  if (error) return <p className="error">{error}</p>
  if (!items) return <p className="muted">Lade Bestellungen …</p>

  return (
    <>
      <h1>Meine Bestellungen</h1>
      <p className="muted">Bestellung {items[0]?.order_number} · geliefert am {new Date(items[0]?.delivered_at ?? '').toLocaleDateString('de-DE')}</p>
      <ul className="list">
        {items.map((item) => (
          <li key={item.id} className="card item">
            <div className="thumb" aria-hidden="true">{CATEGORY_LABELS[item.category].slice(0, 1)}</div>
            <div className="grow">
              <div className="brand">{item.brand}</div>
              <div>{item.title}</div>
              <div className="muted small">Größe {item.size} · {CATEGORY_LABELS[item.category]} · {formatMoney(item.price)}</div>
            </div>
            <button className="btn secondary" onClick={() => navigate(`/return/${item.id}`, { state: item })}>
              Zurückgeben
            </button>
          </li>
        ))}
      </ul>
    </>
  )
}
