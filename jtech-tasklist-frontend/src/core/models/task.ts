export type Task = {
  id: string
  userId: string
  listId: string
  title: string
  description?: string
  completed: boolean
  dueDate?: string
  createdAt?: string
  updatedAt?: string
}
