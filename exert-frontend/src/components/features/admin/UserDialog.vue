<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="title" 
    width="600px" 
    @close="handleClose"
    :close-on-click-modal="false"
  >
    <div class="user-dialog-container">
      <div v-if="dataLoading && mode === 'edit'" class="loading-state">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
        <span>正在加载用户数据...</span>
      </div>

      <el-form 
        v-else
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px" 
        class="user-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                :disabled="mode === 'edit'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select 
                v-model="form.role" 
                placeholder="选择用户角色" 
                style="width: 100%"
                :disabled="mode === 'edit' && form.role === 'SUPER_ADMIN'"
              >
                <el-option label="超管" value="SUPER_ADMIN" />
                <el-option label="管理员" value="ADMIN" />
                <el-option label="专家" value="EXPERT" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态" prop="status">
              <el-select v-model="form.status" placeholder="选择账号状态" style="width: 100%">
                <el-option label="活跃" value="ACTIVE" />
                <el-option label="禁用" value="INACTIVE" />
                <el-option label="待激活" value="PENDING" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="mode === 'add'">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                show-password 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请确认密码" 
                show-password 
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-else>
          <el-col :span="24">
            <el-form-item label="重置密码">
              <el-button 
                type="warning" 
                @click="handleResetPassword"
                :loading="resettingPassword"
              >
                重置密码
              </el-button>
              <div class="password-tips">重置后密码将设置为：123456</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" :disabled="loading">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          {{ mode === 'add' ? '添加' : '保存' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getUserByIdAPI, addUserAPI, updateUserAPI, resetPasswordAPI } from '@/api/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'add'
  },
  userId: {
    type: [Number, String],
    default: null
  },
  defaultRole: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)
const loading = ref(false)
const dataLoading = ref(false)
const resettingPassword = ref(false)

// 表单数据
const form = reactive({
  username: '',
  name: '',
  email: '',
  phone: '',
  role: props.defaultRole || 'ADMIN',
  status: 'ACTIVE',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 255, message: '姓名长度在 2 到 255 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择账号状态', trigger: 'change' }
  ],
  password: [
    { 
      required: props.mode === 'add', 
      message: '请输入密码', 
      trigger: 'blur' 
    },
    { 
      min: 6, 
      message: '密码长度不能少于6位', 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { 
      required: props.mode === 'add', 
      message: '请确认密码', 
      trigger: 'blur' 
    },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 弹窗标题
const title = computed(() => {
  return props.mode === 'add' ? '添加用户' : '编辑用户'
})

// 获取用户详情
const fetchUserDetail = async () => {
  if (!props.userId) {
    console.warn('没有用户ID')
    return
  }

  try {
    dataLoading.value = true
    const response = await getUserByIdAPI(props.userId)
    if (response.success) {
      Object.keys(form).forEach(key => {
        if (response.data[key] !== undefined && response.data[key] !== null) {
          form[key] = response.data[key]
        }
      })
    } else {
      ElMessage.error(response.message || '获取用户详情失败')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  } finally {
    dataLoading.value = false
  }
}

// 重置密码
const handleResetPassword = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置该用户的密码吗？重置后密码将设置为：123456',
      '重置密码确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    resettingPassword.value = true
    const response = await resetPasswordAPI(props.userId)
    if (response.success) {
      ElMessage.success('重置密码成功')
    } else {
      ElMessage.error(response.message || '重置密码失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  } finally {
    resettingPassword.value = false
  }
}

// 保存用户
const handleSave = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) {
      ElMessage.error('请完善用户信息')
      return
    }

    loading.value = true

    const submitData = { ...form }
    
    // 移除确认密码字段
    delete submitData.confirmPassword

    // 如果是编辑模式，添加用户ID
    if (props.mode === 'edit' && props.userId) {
      submitData.userId = props.userId
    }

    let response
    if (props.mode === 'add') {
      response = await addUserAPI(submitData)
    } else {
      response = await updateUserAPI(submitData)
    }

    if (response.success) {
      ElMessage.success(props.mode === 'add' ? '添加用户成功' : '更新用户成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.message || (props.mode === 'add' ? '添加用户失败' : '更新用户失败'))
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error((props.mode === 'add' ? '添加用户失败: ' : '更新用户失败: ') + error.message)
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  resetForm()
  dialogVisible.value = false
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    if (key === 'role') {
      form[key] = props.defaultRole || 'ADMIN'
    } else if (key === 'status') {
      form[key] = 'ACTIVE'
    } else {
      form[key] = ''
    }
  })

  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
  if (newVal) {
    resetForm()
    if (props.mode === 'edit' && props.userId) {
      nextTick(() => {
        fetchUserDetail()
      })
    }
  }
})

// 监听用户ID变化
watch(() => props.userId, (newId) => {
  if (newId && dialogVisible.value && props.mode === 'edit') {
    fetchUserDetail()
  }
})

// 监听默认角色变化
watch(() => props.defaultRole, (newRole) => {
  if (newRole) {
    form.role = newRole
  }
})
</script>

<style scoped>
.user-dialog-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 10px 0;
}

.user-form {
  padding: 0 10px;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading-state .el-icon {
  margin-right: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.password-tips {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 滚动条样式 */
.user-dialog-container::-webkit-scrollbar {
  width: 6px;
}

.user-dialog-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.user-dialog-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.user-dialog-container::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>