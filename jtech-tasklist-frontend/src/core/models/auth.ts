export type LoginPayload = { email: string; password: string }
export type AuthUser = { id: string; name: string; email: string }
export type AuthTokens = { accessToken: string; refreshToken?: string }
