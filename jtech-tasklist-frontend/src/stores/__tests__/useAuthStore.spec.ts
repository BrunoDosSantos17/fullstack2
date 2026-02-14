import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../useAuthStore'
import router from '@/router/router.ts'

// Mock router
vi.mock('@/router/router', () => ({
  default: {
    push: vi.fn(),
  },
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should have initial state', () => {
    const store = useAuthStore()
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
    expect(store.refreshToken).toBeNull()
    expect((store as any).isAuthenticated).toBe(false)
  })

  it('should login and set state', async () => {
    const store = useAuthStore()
    await (store as any).login('test@example.com', 'password')
    expect(store.user).toBeDefined()
    expect(store.user?.email).toBe('test@example.com')
    expect(store.token).toBe('mock-access-token')
    expect(store.refreshToken).toBe('mock-refresh-token')
    expect((store as any).isAuthenticated).toBe(true)
  })

  it('should logout and clear state', async () => {
    const store = useAuthStore()
    await (store as any).login('test@example.com', 'password')
    expect((store as any).isAuthenticated).toBe(true)

    await (store as any).logout()
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
    expect(store.refreshToken).toBeNull()
    expect((store as any).isAuthenticated).toBe(false)

    expect(router.push).toHaveBeenCalledWith('/login')
  })

  it('should refresh token', async () => {
    const store = useAuthStore()
    await (store as any).login('test@example.com', 'password')
    const oldToken = store.token

    await (store as any).refresh()
    expect(store.token).toBe('mock-token-mock-refresh-token')
    expect(store.token).not.toBe(oldToken)
  })

  it('should throw error on refresh without refreshToken', async () => {
    const store = useAuthStore()
    await expect((store as any).refresh()).rejects.toThrow('No refresh token')
  })
})
