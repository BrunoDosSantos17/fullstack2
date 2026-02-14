import type { Tasklist } from '@/core/models/tasklist'
export interface ITasklistService {
  listByUser(userId: string): Promise<Tasklist[]>
  create(list: { userId: string; name: string }): Promise<Tasklist>
  rename(list: { id: string; userId: string; name: string }): Promise<Tasklist>
  remove(id: string): Promise<void>
}
