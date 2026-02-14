import { describe, it, expect, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useTasksStore } from '../useTasksStore'
import { useAuthStore } from '../useAuthStore'

describe('useTasksStore', () => {
  let authStore: ReturnType<typeof useAuthStore>
  let tasksStore: ReturnType<typeof useTasksStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    tasksStore = useTasksStore()
    // Set a mock user
    authStore.user = { id: 'user-1', name: 'Test User', email: 'test@example.com' }
  })

  it('should have initial state', () => {
    expect(tasksStore.items).toEqual([])
  })

  it('should fetch tasks for a list', async () => {
    const listId = 'list-1'
    await (tasksStore as any).fetch(listId)
    expect(tasksStore.items).toEqual([])
  })

  it('should create a task', async () => {
    const listId = 'list-1'
    const title = 'Test Task'
    const description = 'Description'
    const dueDate = '2023-12-31'

    await (tasksStore as any).create(listId, title, description, dueDate)
    expect(tasksStore.items).toHaveLength(1)
    const task = tasksStore.items[0]
    expect(task.title).toBe(title)
    expect(task.description).toBe(description)
    expect(task.dueDate).toBe(dueDate)
    expect(task.completed).toBe(false)
    expect(task.listId).toBe(listId)
    expect(task.userId).toBe('user-1')
  })

  it('should toggle task completion', async () => {
    const listId = 'list-1'
    await (tasksStore as any).create(listId, 'Task 1')
    const taskId = tasksStore.items[0].id

    await (tasksStore as any).toggle(taskId, true)
    expect(tasksStore.items[0].completed).toBe(true)

    await (tasksStore as any).toggle(taskId, false)
    expect(tasksStore.items[0].completed).toBe(false)
  })

  it('should update a task', async () => {
    const listId = 'list-1'
    await (tasksStore as any).create(listId, 'Task 1')
    const task = tasksStore.items[0]

    const updatedTask = { ...task, title: 'Updated Title' }
    await (tasksStore as any).update(updatedTask)
    expect(tasksStore.items[0].title).toBe('Updated Title')
  })

  it('should remove a task', async () => {
    const listId = 'list-1'
    await (tasksStore as any).create(listId, 'Task 1')
    expect(tasksStore.items).toHaveLength(1)
    const taskId = tasksStore.items[0].id

    await (tasksStore as any).remove(taskId)
    expect(tasksStore.items).toEqual([])
  })
})
