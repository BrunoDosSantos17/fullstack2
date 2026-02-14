import axios from 'axios'
import { useAuthStore } from '@/stores/useAuthStore.ts'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use(config => {
  if (!config.url?.includes('/auth/login') && !(config.url === '/auth/register' && config.method === 'post') && !config.url?.includes('/auth/refresh-token')) {
    let token: string | null = null
    try {
      const store = useAuthStore()
      token = store.token
    } catch {
      token = JSON.parse(localStorage.getItem('auth') || '{}').token || null
    }
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
    }
  }
  return config
})

let isRefreshing = false
let failedQueue: any[] = []

function processQueue(error: any, token: string | null = null) {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

http.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    const store = useAuthStore()
    if (error.status === 401 && store.refreshToken && !originalRequest._retry && !originalRequest.url.includes('/auth/login') && !originalRequest.url.includes('/auth/refresh-token')) {
      if (isRefreshing) {
        return new Promise(function (resolve, reject) {
          failedQueue.push({ resolve, reject })
        })
          .then(token => {
            originalRequest.headers['Authorization'] = 'Bearer ' + token
            return http(originalRequest)
          })
          .catch(err => Promise.reject(err))
      }
      originalRequest._retry = true
      isRefreshing = true
      try {
        await store.refresh()
        processQueue(null, store.token)
        originalRequest.headers['Authorization'] = 'Bearer ' + store.token
        return http(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError, null)
        await store.logout()
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
    const message = error.response?.data?.message || 'Erro ao conectar com servidor'
    return Promise.reject({ message, status: error.response?.status })
  }
)

export default http
