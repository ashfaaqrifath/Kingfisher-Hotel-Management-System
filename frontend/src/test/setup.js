import WS from 'ws'
import '@testing-library/jest-dom/vitest'

if (!globalThis.WebSocket) {
  globalThis.WebSocket = WS
}

if (!globalThis.URL.createObjectURL) {
  globalThis.URL.createObjectURL = () => 'blob:mock-url'
}
if (!globalThis.URL.revokeObjectURL) {
  globalThis.URL.revokeObjectURL = () => { }
}
