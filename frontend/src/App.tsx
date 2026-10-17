import { Link, Route, Routes } from 'react-router-dom'
import { useApi } from './useApi'
import OrdersPage from './pages/OrdersPage'
import ReturnReasonPage from './pages/ReturnReasonPage'
import OfferPage from './pages/OfferPage'
import VoucherPage from './pages/VoucherPage'
import TailorPortalPage from './pages/TailorPortalPage'

export default function App() {
  const { mode } = useApi()

  return (
    <div className="phone">
      <header className="topbar">
        <Link to="/" className="wordmark" aria-label="Startseite">zalando</Link>
        <span className="badge">TailorFix Prototyp</span>
      </header>
      {mode === 'mock' && <div className="banner">Demo-Modus ohne Backend. Daten bleiben im Browser.</div>}
      <main className="content">
        <Routes>
          <Route path="/" element={<OrdersPage />} />
          <Route path="/return/:itemId" element={<ReturnReasonPage />} />
          <Route path="/offer/:intentId" element={<OfferPage />} />
          <Route path="/voucher/:code" element={<VoucherPage />} />
          <Route path="/tailor" element={<TailorPortalPage />} />
        </Routes>
      </main>
      <footer className="foot">
        <span>FAU · Implementing Innovation WS 26/27 · Gruppe 2 · Studienprototyp, nicht mit Zalando SE verbunden</span>
        <Link to="/tailor">Schneiderei-Portal</Link>
      </footer>
    </div>
  )
}
