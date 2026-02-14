import { describe, it, expect, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useTasklistsStore } from '../useTasklistsStore'
import { useAuthStore } from '../useAuthStore'

describe('useTasklistsStore', () => {
  let authStore: ReturnType<typeof useAuthStore>
  let tasklistsStore: ReturnType<typeof useTasklistsStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    tasklistsStore = useTasklistsStore()
    // Set a mock user
    authStore.user = { id: 'user-1', name: 'Test User', email: 'test@example.com' }
  })

  it('should have initial state', () => {
    expect(tasklistsStore.items).toEqual([])
  })

  it('should fetch tasklists for user', async () => {
    await (tasklistsStore as any).fetch()
    expect(tasklistsStore.items).toEqual([])
  })

  it('should create a tasklist', async () => {
    const name = 'My Tasks'
    await (tasklistsStore as any).create(name)
    expect(tasklistsStore.items).toHaveLength(1)
    const list = tasklistsStore.items[0]
    expect(list.name).toBe(name)
    expect(list.userId).toBe('user-1')
  })

  it('should rename a tasklist', async () => {
    await (tasklistsStore as any).create('Old Name')
    const listId = tasklistsStore.items[0].id
    const newName = 'New Name'

    await (tasklistsStore as any).rename(listId, newName)
    expect(tasklistsStore.items[0].name).toBe(newName)
  })

  it('should remove a tasklist', async () => {
    await (tasklistsStore as any).create('Test List')
    expect(tasklistsStore.items).toHaveLength(1)
    const listId = tasklistsStore.items[0].id

    await (tasklistsStore as any).remove(listId)
    expect(tasklistsStore.items).toEqual([])
  })
})
