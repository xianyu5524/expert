<template>
  <el-dialog v-model="dialogVisible" title="🎉 专家账号创建成功" width="500px" align-center
    :show-close="false" :close-on-click-modal="false" :close-on-press-escape="false">
    <div class="account-success-dialog">
      <div class="success-icon">
        <el-icon size="60" color="#67C23A">
          <CircleCheck />
        </el-icon>
      </div>

      <div class="account-info">
        <h3>专家账号信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">专家姓名：</span>
            <span class="value">{{ name }}</span>
          </div>
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value username">{{ username }}</span>
          </div>
          <div class="info-item">
            <span class="label">初始密码：</span>
            <span class="value password">123456</span>
          </div>
        </div>
      </div>

      <div class="warning-tip">
        <el-icon color="#E6A23C">
          <Warning />
        </el-icon>
        <span>请妥善保存并通知专家及时登录修改密码</span>
      </div>
    </div>

    <template #footer>
      <el-button type="primary" @click="handleConfirm" size="large">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { CircleCheck, Warning } from '@element-plus/icons-vue'
import { computed } from 'vue' 

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  username: {
    type: String,
    default: ''
  },
  name: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const handleConfirm = () => {
  dialogVisible.value = false
  emit('confirm')
}
</script>

<style scoped>
.account-success-dialog {
  text-align: center;
  padding: 10px 0;
}

.account-success-dialog .success-icon {
  margin-bottom: 20px;
}

.account-success-dialog .account-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.account-success-dialog .account-info h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-weight: 600;
  font-size: 16px;
}

.account-success-dialog .info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-success-dialog .info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.account-success-dialog .info-item .label {
  color: #606266;
  font-weight: 500;
  font-size: 14px;
}

.account-success-dialog .info-item .value {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

.account-success-dialog .info-item .username {
  color: #409EFF;
}

.account-success-dialog .info-item .password {
  color: #F56C6C;
}

.account-success-dialog .warning-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #fdf6ec;
  border: 1px solid #faecd8;
  color: #E6A23C;
  padding: 12px;
  border-radius: 6px;
  font-size: 14px;
}

.account-success-dialog .warning-tip .el-icon {
  font-size: 16px;
}
</style>