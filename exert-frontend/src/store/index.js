import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 用户状态管理
export const useUserStore = defineStore('user', () => {
    // 用户信息
    const user = ref({
        username:'',
        role:'',
        token:''
    });

    // 设置用户信息
    const setUser = (userData) => {
        user.value = userData;
        // 将用户信息存储在本地存储中
        localStorage.setItem('userToken', userData.token)
        localStorage.setItem('userRole', userData.role)
        localStorage.setItem('username', userData.username)
    };

    // 清除用户信息
    const clearUser = () => {
    user.value = {
      username: '',
      role: '',
      token: ''
    }
    localStorage.removeItem('userToken')
    localStorage.removeItem('userRole')
    localStorage.removeItem('username')
    };

    // 获取用户信息

    const isAuthenticated = computed(() => !!user.value.token)
    const userRole = computed(() => user.value.role)
    const currentUser = computed(() => user.value)

    return {
    // State
    user,
    
    // Actions
    setUser,
    clearUser,
    
    // Getters
    isAuthenticated,
    userRole,
    currentUser
  }
})

// 仪表盘状态管理
export const useDashboardStore = defineStore('dashboard', () => {
  // 仪表盘统计数据
  const stats = ref({
    expertCount: 0,
    projectCount: 0,
    pendingAppCount: 0,
    birthdayCount: 0
  })

    // 设置统计数据
  const setStats = (newStats) => {
    stats.value = newStats
  }

  return {
    // State
    stats,
    
    // Actions
    setStats
  }
})

// 专家状态管理
export const useExpertStore = defineStore('expert', () => {
  // State
  const experts = ref([])
  const currentExpert = ref(null)

  // Actions
  const setExperts = (expertsList) => {
    experts.value = expertsList
  }

  const setCurrentExpert = (expert) => {
    currentExpert.value = expert
  }

  return {
    // State
    experts,
    currentExpert,
    
    // Actions
    setExperts,
    setCurrentExpert
  }
})


