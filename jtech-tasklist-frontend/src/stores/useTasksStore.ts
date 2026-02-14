import { defineStore } from 'pinia'
import { Services } from '@/core/services'
import type { Task } from '@/core/models/task'
import { useAuthStore } from './useAuthStore'

export const useTasksStore = defineStore('tasks', {
  state: (): { items: Task[] } => ({ items: [] }),
  actions: {
    async fetch(listId: string) {
      this.items = await Services.tasks.listByListId(listId)
    },
    async create(listId: string, title: string, description?: string, dueDate?: string) {


      const userId = useAuthStore().user!.id


      const created = await Services.tasks.create({
        userId,
        listId,
        title,
        description,
        dueDate,
        completed: false,
      })
      this.items.push(created)
    },
    async toggle(id: string, completed: boolean) {
      const upd = await Services.tasks.toggle(id, completed)
      const idx = this.items.findIndex((t) => t.id === id)
      if (idx >= 0) this.items[idx] = upd
    },
    async update(task: Task) {
      const upd = await Services.tasks.update(task)
      const idx = this.items.findIndex((t) => t.id === task.id)
      if (idx >= 0) this.items[idx] = upd
    },
    async remove(id: string) {
      await Services.tasks.remove(id)
      this.items = this.items.filter((t) => t.id !== id)
    },
  },
  persist: true,
})
