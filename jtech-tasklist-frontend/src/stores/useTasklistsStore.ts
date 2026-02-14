import { defineStore } from 'pinia'
import { Services } from '@/core/services'
import type { Tasklist } from '@/core/models/tasklist'
import { useAuthStore } from './useAuthStore'

export const useTasklistsStore = defineStore('tasklists', {
  state: (): { items: Tasklist[] } => ({ items: [] }),
  actions: {
    async fetch() {
      const userId = useAuthStore().user!.id
      this.items = await Services.tasklists.listByUser(userId)
    },
    async create(name: string) {
      const userId = useAuthStore().user!.id
      const created = await Services.tasklists.create({ userId, name })
      this.items.push(created)
    },
    async rename(id: string, name: string) {
      const userId = useAuthStore().user!.id
      const updated = await Services.tasklists.rename({ id, userId, name })
      const idx = this.items.findIndex((l) => l.id === id)
      if (idx >= 0) this.items[idx] = updated
    },
    async remove(id: string) {
      await Services.tasklists.remove(id)
      this.items = this.items.filter((l) => l.id !== id)
    },
  },
  persist: true,
})
