import { describe, it, expect } from 'vitest'
import { validateEmail } from '../lib/validation'

describe('validateEmail', () => {
    it('accepts a valid non-Gmail address', () => {
        const result = validateEmail('user@outlook.com')
        expect(result).toEqual({ valid: true, value: 'user@outlook.com' })
    })

    it('rejects malformed email addresses', () => {
        const result = validateEmail('not-an-email')
        expect(result).toEqual({ valid: false, error: 'Please enter a valid email address.' })
    })
})
