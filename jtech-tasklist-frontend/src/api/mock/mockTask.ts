import type { ITaskService } from '../../core/services/ITaskService.ts'
import type { Task } from '@/core/models/task.ts'
import { sget, sset, suuid, nowIso } from '@/core/utils/storage.ts'

const KEY = 'mock_tasks' // array global de tarefas

export class MockTask implements ITaskService {
  private load() {
    return sget<Task[]>(KEY, [])
  }
  private save(v: Task[]) {
    sset(KEY, v)
  }

  async listByListId(listId: string) {
    return this.load().filter((t) => t.listId === listId)
  }

  async create(t: Omit<Task, 'id' | 'completed'> & { completed?: boolean }) {
    const created: Task = {
      ...t,
      id: suuid(),
      completed: !!t.completed,
      createdAt: nowIso(),
      updatedAt: nowIso(),
    }
    this.save([...this.load(), created])
    return created
  }

  async update(t: Task) {
    const all = this.load()
    const idx = all.findIndex((x) => x.id === t.id)
    if (idx < 0) throw new Error('Task não encontrada.')
    all[idx] = { ...t, updatedAt: nowIso() }
    this.save(all)
    return all[idx]
  }

  async toggle(id: string, completed: boolean) {
    const all = this.load()
    const idx = all.findIndex((t) => t.id === id)
    if (idx < 0) throw new Error('Task não encontrada.')
    all[idx] = { ...all[idx], completed, updatedAt: nowIso() }
    this.save(all)
    return all[idx]
  }

  async remove(id: string) {
    this.save(this.load().filter((t) => t.id !== id))
  }
}
