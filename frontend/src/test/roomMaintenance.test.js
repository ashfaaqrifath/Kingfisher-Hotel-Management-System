import { beforeEach, describe, expect, it } from 'vitest'
import { MAINTENANCE_DURATION_MS, reconcileMaintenanceRooms } from '../lib/roomMaintenance'

function makeStorage(initial = {}) {
    const store = new Map(Object.entries(initial))
    return {
        getItem(key) {
            return store.has(key) ? store.get(key) : null
        },
        setItem(key, value) {
            store.set(key, String(value))
        },
        removeItem(key) {
            store.delete(key)
        },
        clear() {
            store.clear()
        },
        key(index) {
            return Array.from(store.keys())[index] ?? null
        },
        get length() {
            return store.size
        },
    }
}

describe('room maintenance window', () => {
    beforeEach(() => {
        window.localStorage.clear()
    })

    it('releases expired maintenance rooms back to available', async () => {
        const updates = []
        const storage = makeStorage({
            'kingfisher:maintenance-room:room-1': String(Date.now() - MAINTENANCE_DURATION_MS - 1000),
        })

        const supabase = {
            from(table) {
                if (table !== 'rooms') return null
                return {
                    update(payload) {
                        updates.push(payload)
                        return {
                            eq() {
                                return Promise.resolve({ data: null, error: null })
                            },
                        }
                    },
                }
            },
        }

        await reconcileMaintenanceRooms(supabase, { storage, now: Date.now() })

        expect(updates).toEqual([{ status: 'Available' }])
    })
})
