const MAINTENANCE_DURATION_MS = 3 * 60 * 60 * 1000
// For test, replace with 15 * 1000
const STORAGE_PREFIX = 'kingfisher:maintenance-room:'

function getMaintenanceKey(roomId) {
    return `${STORAGE_PREFIX}${roomId}`
}

function scheduleRoomMaintenance(storage, roomId, now = Date.now()) {
    if (!roomId) return
    storage.setItem(getMaintenanceKey(roomId), String(now))
}

function clearRoomMaintenance(storage, roomId) {
    if (!roomId) return
    storage.removeItem(getMaintenanceKey(roomId))
}

function getMaintenanceTimestamp(storage, roomId) {
    if (!roomId) return null
    const value = storage.getItem(getMaintenanceKey(roomId))
    if (!value) return null
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : null
}

async function reconcileMaintenanceRooms(supabase, options = {}) {
    const storage = options.storage || window.localStorage
    const now = options.now ?? Date.now()

    if (!supabase?.from) return []

    const keys = []
    for (let index = 0; index < storage.length; index += 1) {
        const key = storage.key(index)
        if (!key || !key.startsWith(STORAGE_PREFIX)) continue
        keys.push(key)
    }

    const expiredRoomIds = keys
        .map((key) => key.replace(STORAGE_PREFIX, ''))
        .filter((roomId) => {
            const timestamp = getMaintenanceTimestamp(storage, roomId)
            if (timestamp === null) return false
            return now - timestamp >= MAINTENANCE_DURATION_MS
        })

    if (expiredRoomIds.length === 0) return []

    const results = []
    for (const roomId of expiredRoomIds) {
        const { error } = await supabase.from('rooms').update({ status: 'Available' }).eq('id', roomId)
        if (!error) {
            clearRoomMaintenance(storage, roomId)
            results.push(roomId)
        }
    }

    return results
}

export {
    MAINTENANCE_DURATION_MS,
    clearRoomMaintenance,
    getMaintenanceTimestamp,
    reconcileMaintenanceRooms,
    scheduleRoomMaintenance,
}
