import type { IAuthService } from '../../core/services/IAuthService.ts'
import type { AuthUser, LoginPayload } from '@/core/models/auth.ts'
import { sget, sset, suuid } from '@/core/utils/storage.ts'
const KEY = 'mock_users'
export class MockAuth implements IAuthService {
  async login({ email, password }: LoginPayload) {
    if (!email || !password) throw new Error('Credenciais inválidas')
    let users = sget<AuthUser[]>(KEY, [])
    let user = users.find((u) => u.email === email)
    if (!user) {
      user = { id: suuid(), name: email.split('@')[0], email }
      users = [...users, user]
      sset(KEY, users)
    }
    return { user, token: 'mock-access-token', refreshToken: 'mock-refresh-token' }
  }
  async logout(): Promise<void> {
  }

  async refreshToken(refreshToken: string) {
    return { token: 'mock-token-' + refreshToken }
  }
  async register(user: { name: string; email: string; password: string }) {
    let users = sget<AuthUser[]>(KEY, [])
    if (users.some(u => u.email === user.email)) {
      throw new Error('Já existe um usuário cadastrado com este email.')
    }
    const newUser = { id: suuid(), name: user.name, email: user.email }
    users = [...users, newUser]
    sset(KEY, users)
    return newUser
  }
  async me() {
    return null
  }
}
