import type { LoginPayload, AuthUser, AuthTokens } from '@/core/models/auth'
export interface IAuthService {
  login(payload: LoginPayload): Promise<{ user: AuthUser; tokens?: AuthTokens }>
  logout(): Promise<void>
  me(): Promise<AuthUser | null>
}
