<!-- components/common/BatchOperationDialog.vue -->
<template>
  <el-dialog v-model="visible" :title="title" width="600px">
    <div class="batch-operation-dialog">
      <!-- 操作步骤 -->
      <el-steps :active="currentStep" align-center class="steps">
        <el-step title="下载模板" />
        <el-step title="填写数据" />
        <el-step title="上传文件" />
      </el-steps>

      <!-- 操作区域 -->
      <div class="operation-area">
        <!-- 模板下载 -->
        <div v-if="currentStep === 0" class="step-content">
          <div class="template-info">
            <el-icon size="48" color="#409EFF"><Document /></el-icon>
            <div class="info-text">
              <h4>下载导入模板</h4>
              <p>请先下载模板文件，按照模板格式填写数据</p>
            </div>
          </div>
          <div class="action-buttons">
            <el-button type="primary" @click="downloadTemplate">
              <el-icon><Download /></el-icon>
              下载模板
            </el-button>
          </div>
        </div>

        <!-- 文件上传 -->
        <div v-if="currentStep === 2" class="step-content">
          <el-upload
            ref="uploadRef"
            class="upload-demo"
            drag
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".xlsx, .xls"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 xlsx/xls 文件，且不超过10MB
              </div>
            </template>
          </el-upload>

          <!-- 导入进度 -->
          <div v-if="importProgress.visible" class="import-progress">
            <el-progress 
              :percentage="importProgress.percentage" 
              :status="importProgress.status"
              :text-inside="true"
              :stroke-width="20"
            />
            <div class="progress-text">{{ importProgress.text }}</div>
          </div>

          <!-- 导入结果 -->
          <div v-if="importResult" class="import-result">
            <el-alert
              :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failureCount} 条`"
              :type="importResult.failureCount === 0 ? 'success' : 'warning'"
              :closable="false"
            />
            
            <!-- 账号信息下载 -->
            <div v-if="importResult.accountFileUrl" class="account-download">
              <el-alert
                title="专家账号信息已生成，请下载并妥善保管"
                type="info"
                :closable="false"
                style="margin-top: 15px;"
              />
              <div class="download-action">
                <el-button type="success" @click="downloadAccountInfo">
                  <el-icon><Download /></el-icon>
                  下载账号信息
                </el-button>
                <span class="download-tip">包含用户名和初始密码等信息</span>
              </div>
            </div>

            <!-- 错误列表 -->
            <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
              <h5>错误详情：</h5>
              <ul>
                <li v-for="(error, index) in importResult.errors" :key="index">
                  第{{ error.row }}行: {{ error.message }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button v-if="currentStep === 0" type="primary" @click="currentStep = 2">
          跳过，直接上传
        </el-button>
        <el-button 
          v-if="currentStep === 2 && selectedFile && !importResult" 
          type="primary" 
          @click="handleImport"
          :loading="importing"
        >
          开始导入
        </el-button>
        <el-button 
          v-if="currentStep === 2 && importResult" 
          type="primary" 
          @click="handleFinish"
        >
          完成
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download, UploadFilled } from '@element-plus/icons-vue'
import batchService from '@/composables/batchService'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  type: {
    type: String, // 'expert' 或 'project'
    required: true
  },
  title: {
    type: String,
    default: '批量操作'
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const currentStep = ref(0)
const uploadRef = ref(null)
const fileList = ref([])
const selectedFile = ref(null)
const importing = ref(false)
const importResult = ref(null)

const importProgress = reactive({
  visible: false,
  percentage: 0,
  status: 'success',
  text: ''
})

// 下载模板
const downloadTemplate = async () => {
  const templateName = props.type === 'expert' ? '专家信息' : '项目信息'
  await batchService.downloadTemplate(props.type, templateName)
}

// 文件选择
const handleFileChange = (file) => {
  selectedFile.value = file.raw
  importResult.value = null
}

// 执行导入
const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importing.value = true
  importProgress.visible = true
  importProgress.percentage = 0
  importProgress.text = '正在解析文件...'

  try {
    // 模拟进度更新
    const progressInterval = setInterval(() => {
      if (importProgress.percentage < 90) {
        importProgress.percentage += 10
        importProgress.text = `正在导入数据... ${importProgress.percentage}%`
      }
    }, 200)

    const result = await batchService.importData(props.type, selectedFile.value, (progress) => {
      importProgress.percentage = progress
    })

    clearInterval(progressInterval)
    importProgress.percentage = 100
    importProgress.text = '导入完成'

    importResult.value = result

    if (result.successCount > 0) {
      ElMessage.success(`成功导入 ${result.successCount} 条数据`)
      emit('success')
    }

    if (result.failureCount > 0) {
      ElMessage.warning(`${result.failureCount} 条数据导入失败，请查看错误详情`)
    }

  } catch (error) {
    console.error('导入失败:', error)
    importProgress.status = 'exception'
    importProgress.text = '导入失败'
    ElMessage.error('导入失败: ' + error.message)
  } finally {
    importing.value = false
  }
}

// 下载账号信息
const downloadAccountInfo = () => {
  if (importResult.value && importResult.value.accountFileUrl) {
    // 创建隐藏的下载链接
    const link = document.createElement('a')
    link.href = importResult.value.accountFileUrl
    link.download = '专家账号信息.xlsx'
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}
// 完成操作
const handleFinish = () => {
  resetDialog()
  visible.value = false
}

// 取消操作
const handleCancel = () => {
  resetDialog()
  visible.value = false
}

// 重置对话框状态
const resetDialog = () => {
  currentStep.value = 0
  fileList.value = []
  selectedFile.value = null
  importResult.value = null
  importProgress.visible = false
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}
</script>

<style scoped>
.batch-operation-dialog {
  padding: 20px 0;
}

.steps {
  margin-bottom: 30px;
}

.step-content {
  text-align: center;
  padding: 20px;
}

.template-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 20px;
}

.info-text h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.info-text p {
  margin: 0;
  color: #909399;
}

.action-buttons {
  margin-top: 20px;
}

.upload-demo {
  margin-bottom: 20px;
}

.import-progress {
  margin: 20px 0;
}

.progress-text {
  margin-top: 8px;
  color: #606266;
  font-size: 14px;
}

.import-result {
  margin-top: 20px;
}

.account-download {
  margin-top: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.download-action {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.download-tip {
  color: #909399;
  font-size: 12px;
}

.error-list {
  margin-top: 10px;
  text-align: left;
}

.error-list h5 {
  margin: 10px 0 5px 0;
  color: #f56c6c;
}

.error-list ul {
  margin: 0;
  padding-left: 20px;
  color: #f56c6c;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>