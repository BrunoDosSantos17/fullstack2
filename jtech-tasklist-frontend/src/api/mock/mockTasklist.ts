import type { ITasklistService } from '../../core/services/ITasklistService.ts'
import type { Tasklist } from '@/core/models/tasklist.ts'
import { sget, sset, suuid } from '@/core/utils/storage.ts'
import type { Task } from '@/core/models/task.ts'

const KEY = 'mock_tasklists' // array global de listas

export class MockTasklist implements ITasklistService {
  private load() {
    return sget<Tasklist[]>(KEY, [])
  }
  private save(v: Tasklist[]) {
    sset(KEY, v)
  }

  async listByUser(userId: string) {
    return this.load().filter((l) => l.userId === userId)
  }

  async create(list: { userId: string; name: string }) {
    const all = this.load()
    if (all.some((l) => l.userId === list.userId && l.name.toLowerCase() === list.name.toLowerCase()))
      throw new Error('Já existe uma lista com esse nome.')
    const created = { id: suuid(), userId: list.userId, name: list.name }
    this.save([...all, created])
    return created
  }

  async rename(list: { id: string; userId: string; name: string }) {
    const all = this.load()
    const idx = all.findIndex((l) => l.id === list.id && l.userId === list.userId)
    if (idx < 0) throw new Error('Lista não encontrada.')
    all[idx] = { ...all[idx], name: list.name }
    this.save(all)
    return all[idx]
  }

  async remove(id: string) {
    this.save(this.load().filter((l) => l.id !== id))
    const TKEY = 'mock_tasks'
    const tasks = sget<Task[]>(TKEY, [])
    sset(
      TKEY,
      tasks.filter((t) => t.listId !== id),
    )
  }
}
