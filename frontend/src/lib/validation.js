/**
 * Validation functions for form fields
 */

export function validateFullName(fullName) {
  const trimmed = String(fullName || '').trim()

  if (!trimmed) {
    return { valid: false, error: 'Please enter a valid full name.' }
  }

  if (trimmed.length < 3) {
    return { valid: false, error: 'Please enter a valid full name.' }
  }

  const nameRegex = /^[a-zA-Z\s]+$/
  if (!nameRegex.test(trimmed)) {
    return { valid: false, error: 'Please enter a valid full name.' }
  }

  return { valid: true, value: trimmed }
}

export function validateEmail(email) {
  const trimmed = String(email || '').trim()

  if (!trimmed) {
    return { valid: false, error: 'Please enter a valid email address.' }
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(trimmed)) {
    return { valid: false, error: 'Please enter a valid email address.' }
  }

  return { valid: true, value: trimmed }
}

export function validatePhoneNumber(phone) {
  const trimmed = String(phone || '').trim()

  if (!trimmed) {
    return { valid: false, error: 'Phone number must contain exactly 10 digits.' }
  }

  const phoneRegex = /^\d{10}$/
  if (!phoneRegex.test(trimmed)) {
    return { valid: false, error: 'Phone number must contain exactly 10 digits.' }
  }

  return { valid: true, value: trimmed }
}

export function validateSalary(salary) {
  const numValue = Number(salary)

  if (isNaN(numValue) || numValue <= 0) {
    return { valid: false, error: 'Monthly salary must be greater than 0.' }
  }

  return { valid: true, value: numValue }
}
