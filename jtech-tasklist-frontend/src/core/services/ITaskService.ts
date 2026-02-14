import type { Task } from '@/core/models/task'
export interface ITaskService {
  listByListId(listId: string): Promise<Task[]>
  create(t: Omit<Task,'id'|'completed'> & { completed?: boolean }): Promise<Task>
  update(t: Task): Promise<Task>
  toggle(id: string, completed: boolean): Promise<Task>
  remove(id: string): Promise<void>
}
