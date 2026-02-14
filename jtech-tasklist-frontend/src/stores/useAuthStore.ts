import { defineStore } from 'pinia'
import { Services } from '@/core/services'
import router from '@/router/router.ts'
import type { AuthUser } from '@/core/models/auth'

export const useAuthStore = defineStore('auth', {
  state: (): { user: AuthUser | null, token: string | null, refreshToken: string | null } => ({ user: null, token: null, refreshToken: null }),
  getters: {
    isAuthenticated: (s) => !!s.user && !!s.token && !!s.refreshToken,
  },
  actions: {
    async login(email: string, password: string) {
      const { user, token, refreshToken } = await Services.auth.login({ email, password })
      this.user = user
      this.token = token
      this.refreshToken = refreshToken
    },
    async logout() {
      if (this.refreshToken) {
        await Services.auth.logout(this.refreshToken)
      }
      this.user = null
      this.token = null
      this.refreshToken = null
      router.push('/login')
    },
    async refresh() {
      if (!this.refreshToken) throw new Error('No refresh token')
      const { token } = await Services.auth.refreshToken(this.refreshToken)
      this.token = token
    },
  },
  persist: true,
})
