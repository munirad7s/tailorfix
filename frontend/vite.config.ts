import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// /api/* is proxied to the Kotlin backend in development; in the Docker image nginx does the same.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
