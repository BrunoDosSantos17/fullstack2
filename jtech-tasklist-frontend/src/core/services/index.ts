import { MockAuth } from '@/api/mock/mockAuth.ts'
import { MockTasklist } from '@/api/mock/mockTasklist.ts'
import { MockTask } from '@/api/mock/mockTask.ts'
import { AuthApi } from '@/api/authApi.ts'
import { TasklistApi } from '@/api/tasklistApi.ts'
import { TaskApi } from '@/api/taskApi.ts'

const useMock = (import.meta.env.VITE_USE_MOCK ?? 'true') === 'true'

export const Services = {
  auth: useMock ? new MockAuth() : new AuthApi(),
  tasklists: useMock ? new MockTasklist() : new TasklistApi(),
  tasks: useMock ? new MockTask() : new TaskApi(),
} as const

export const usingMock = useMock
