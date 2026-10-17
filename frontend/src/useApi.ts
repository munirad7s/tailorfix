import { useEffect, useState } from 'react'
import { resolveApi, type ApiMode, type TailorFixApi } from './api'

export function useApi(): { api: TailorFixApi | null; mode: ApiMode | null } {
  const [state, setState] = useState<{ api: TailorFixApi; mode: ApiMode } | null>(null)
  useEffect(() => {
    let cancelled = false
    resolveApi().then((r) => {
      if (!cancelled) setState(r)
    })
    return () => {
      cancelled = true
    }
  }, [])
  return { api: state?.api ?? null, mode: state?.mode ?? null }
}
