<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">高校专家库系统</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password
            size="large"></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" size="large" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
        <el-form-item style="text-align: center">
          <el-link type="primary" @click="$router.push('/register')">专家注册</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { ElMessage } from 'element-plus'
import { loginAPI } from '../api/login'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据model
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}


// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    await loginFormRef.value.validate()

    // 开始登录
    loading.value = true
    const loginData = {
      username: loginForm.username,
      password: loginForm.password
    }

    const response = await loginAPI(loginData)

    // 后端状态检查
    if (response.code !== 200 || !response.success) {
      ElMessage.error(response.data.message || '登录失败')
      return
    }
    const user = {
      token: response.data.token,
      userId: response.data.userInfo.userId,
      username: response.data.userInfo.username,
      email: response.data.userInfo.email,
      phone: response.data.userInfo.phone,
      role: response.data.userInfo.role,
      status: response.data.userInfo.status,
      lastLoginTime: response.data.userInfo.lastLoginTime
    }

    if (loginForm.remember) {
      // 长期存储
      localStorage.setItem('userToken', user.token)
    } else {
      // 会话存储（浏览器关闭后清除）
      sessionStorage.setItem('userToken', user.token)
    }
    userStore.setUser(user)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    //区分错误类型
    if (error?.fields) {
      // 这是表单验证错误，Element Plus 已经显示了具体错误，可以不用额外提示
      console.log('表单验证失败:', error.fields)
    } else {
      // 这是网络请求错误
      console.error('网络请求失败:', error)
      ElMessage.error('登录失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 20px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}
</style>