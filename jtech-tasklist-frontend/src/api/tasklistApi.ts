import http from '@/api/apiClient.ts'
import { useAuthStore } from '@/stores/useAuthStore.ts'

function getUserIdHeader() {
  const user = useAuthStore().user
  return user ? { 'X-User-Id': user.id } : {}
}

export class TasklistApi {
  async listByUser(userId: string) {
    const headers = getUserIdHeader()
    const { data } = await http.get(`/tasklists/user/${userId}`, { headers })
    return data
  }

  async create(list: { userId: string; name: string }) {
    const { data } = await http.post('/tasklists', list)
    return data
  }

  async rename(list: { id: string; userId: string; name: string }) {
    const headers = getUserIdHeader()
    const { data } = await http.put(`/tasklists/${list.id}`, list, { headers })
    return data
  }

  async remove(id: string) {
    const headers = getUserIdHeader()
    await http.delete(`/tasklists/${id}`, { headers })
  }
}
