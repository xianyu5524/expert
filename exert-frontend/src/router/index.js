import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store'

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    {
      path: '/',
      component: () => import('@/components/Layout/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
        { path: 'experts', name: 'Experts', component: () => import('@/views/ExpertsView.vue') },
        { path: 'projects', name: 'Projects', component: () => import('@/views/ProjectsView.vue') },
        { path: 'recommendations', name: 'Recommendations', component: () => import('@/views/RecommendationsView.vue') },
        { path: 'applications', name: 'Applications', component: () => import('@/views/Applications.vue') },
        { path: 'reminders', name: 'Reminders', component: () => import('@/views/Reminders.vue') },
        { path: 'admin-management', name: 'AdminManagement', component: () => import('@/views/AdminManagementView.vue'), meta: { requiresAdmin: true, allowedRoles: ['SUPER_ADMIN', 'ADMIN']} },
        { path: 'system-config', name: 'SystemConfig', component: () => import('@/views/SystemConfig.vue'), meta: { requiresAdmin: true } },
        { path: 'operation-logs', name: 'OperationLogs', component: () => import('@/views/OperationLogs.vue'), meta: { requiresAdmin: true } }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 获取用户状态
  const userStore = useUserStore()

  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAuthenticated) {
    const userRole = userStore.userRole
    if (userRole !== 'SUPER_ADMIN' && userRole !== 'ADMIN') {
      next('/dashboard')
      return
    }
  }

  // 重定向未认证用户到登录页
  if (to.name !== 'Login' && !userStore.isAuthenticated) {
    next('/login')
  } else if (to.name === 'Login' && userStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

// 导出路由
export default router