import WS from 'ws'
import '@testing-library/jest-dom/vitest'

// jsdom doesn't provide a usable WebSocket; supabase-js constructs a
// RealtimeClient (unused by these tests) at client-creation time and
// throws without one.
if (!globalThis.WebSocket) {
  globalThis.WebSocket = WS
}

// jsdom doesn't implement blob object URLs; reportUtils.js uses these to
// trigger browser file downloads.
if (!globalThis.URL.createObjectURL) {
  globalThis.URL.createObjectURL = () => 'blob:mock-url'
}
if (!globalThis.URL.revokeObjectURL) {
  globalThis.URL.revokeObjectURL = () => {}
}
