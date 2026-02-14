import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/auth/pages/LoginView.vue'
import TasklistsView from '@/views/tasklists/pages/TaskListView.vue'
import { useAuthStore } from '@/stores/useAuthStore.ts'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView, meta: { public: true } },
    { path: '/', name: 'lists', component: TasklistsView },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthenticated) return { name: 'login' }
  if (to.name === 'login' && auth.isAuthenticated) return { name: 'lists' }
})

export default router
