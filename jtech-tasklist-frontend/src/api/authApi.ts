import http from '@/api/apiClient.ts'

export class AuthApi {
  async login({ email, password }: { email: string; password: string }) {
    try {
      const { data } = await http.post('/auth/login', { email, password })
      return data
    } catch (err: any) {
      if (err.status === 404 || err.status === 400) {
        const { data } = await http.post('/auth/register', {
          name: email.split('@')[0],
          email,
          password,
        })
        return data
      }
      throw err
    }
  }

  async getUserById(id: string) {
    const { data } = await http.get(`/auth/${id}`)
    return data
  }

  async logout(refreshToken: string) {
    await http.post('/auth/logout', { refreshToken })
  }

  async refreshToken(refreshToken: string) {
    const { data } = await http.post('/auth/refresh-token', { refreshToken })
    return data
  }

  async register(user: { name: string; email: string; password: string }) {
    const { data } = await http.post('/auth/register', user)
    return data
  }
}
