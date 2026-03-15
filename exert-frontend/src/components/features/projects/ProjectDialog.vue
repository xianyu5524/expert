<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="title" 
    width="800px" 
    @close="handleClose"
    :close-on-click-modal="false"
  >
    <div class="project-dialog-container">
      <!-- 数据加载状态 -->
      <div v-if="dataLoading && mode === 'edit'" class="loading-state">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
        <span>正在加载项目数据...</span>
      </div>

      <el-form 
        v-else
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px" 
        class="project-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目赛道" prop="track">
              <el-select 
                v-model="form.track" 
                placeholder="选择项目赛道" 
                style="width: 100%"
                @change="handleTrackChange"
              >
                <el-option label="高教主赛道" value="MAIN_TRACK" />
                <el-option label="红旅赛道" value="RED_TOURISM_TRACK" />
                <el-option label="产业赛道" value="INDUSTRY_TRACK" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目组别" prop="category">
              <el-select 
                v-model="form.category" 
                placeholder="先选择赛道" 
                style="width: 100%"
                :disabled="!form.track"
              >
                <el-option 
                  v-for="category in currentCategoryOptions" 
                  :key="category.value" 
                  :label="category.label" 
                  :value="category.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目阶段" prop="stage">
              <el-select v-model="form.stage" placeholder="选择项目阶段" style="width: 100%">
                <el-option label="校赛" value="SCHOOL" />
                <el-option label="省赛" value="PROVINCIAL" />
                <el-option label="国赛" value="NATIONAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leaderName">
              <el-input v-model="form.leaderName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级">
              <el-input v-model="form.grade" placeholder="请输入年级" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指导老师">
              <el-input v-model="form.advisor" placeholder="请输入指导老师" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="form.status" placeholder="选择项目状态" style="width: 100%">
                <el-option label="准备中" value="PREPARING" />
                <el-option label="进行中" value="ONGOING" />
                <el-option label="已暂停" value="SUSPENDED" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="已终止" value="TERMINATED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目等级" prop="projectLevel">
              <el-select v-model="form.projectLevel" placeholder="选择项目等级" style="width: 100%">
                <el-option label="重点" value="KEY" />
                <el-option label="一般" value="GENERAL" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="获奖情况">
              <el-select v-model="form.award" placeholder="选择奖项" style="width: 100%" clearable>
                <el-option label="一等奖" value="FIRST_PRIZE" />
                <el-option label="二等奖" value="SECOND_PRIZE" />
                <el-option label="三等奖" value="THIRD_PRIZE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建年份" prop="createdYear">
              <el-date-picker
                v-model="form.createdYear"
                type="year"
                placeholder="选择年份"
                style="width: 100%"
                value-format="YYYY"
                :disabled-date="disabledDate"
              />
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
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getProjectByIdAPI, addProjectAPI, updateProjectAPI } from '@/api/project'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'add' // 'add' 或 'edit'
  },
  projectId: {
    type: [Number, String],
    default: null
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

// 表单数据
const form = reactive({
  name: '',
  track: 'MAIN_TRACK',
  category: 'UNDERGRADUATE_CREATIVE',
  leaderName: '',
  grade: '',
  major: '',
  stage: 'SCHOOL',
  status: 'PREPARING',
  award: '',
  advisor: '',
  projectLevel: 'GENERAL',
  createdYear: new Date().getFullYear().toString()
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 200, message: '项目名称长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  track: [
    { required: true, message: '请选择项目赛道', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择项目组别', trigger: 'change' }
  ],
  leaderName: [
    { required: true, message: '请输入负责人姓名', trigger: 'blur' }
  ],
  stage: [
    { required: true, message: '请选择项目阶段', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ],
  projectLevel: [
    { required: true, message: '请选择项目等级', trigger: 'change' }
  ],
  createdYear: [
    { required: true, message: '请选择创建年份', trigger: 'change' }
  ]
}

// 组别选项配置
const categoryOptions = {
  MAIN_TRACK: [
    { label: '本科生创意组', value: 'UNDERGRADUATE_CREATIVE' },
    { label: '本科生创业组', value: 'UNDERGRADUATE_ENTREPRENEURIAL' },
    { label: '研究生创意组', value: 'POSTGRADUATE_CREATIVE' },
    { label: '研究生创业组', value: 'POSTGRADUATE_ENTREPRENEURIAL' }
  ],
  RED_TOURISM_TRACK: [
    { label: '公益组', value: 'PUBLIC_WELFARE' },
    { label: '创意组', value: 'RED_CREATIVE' },
    { label: '创业组', value: 'RED_ENTREPRENEURIAL' }
  ],
  INDUSTRY_TRACK: [
    { label: '企业命题组', value: 'ENTERPRISE_PROPOSITION' },
    { label: '区域特色产业组', value: 'REGIONAL_INDUSTRY' },
    { label: '成果转化组', value: 'ACHIEVEMENT_TRANSFORMATION' }
  ]
}

// 计算当前可选的组别
const currentCategoryOptions = computed(() => {
  if (!form.track) return []
  return categoryOptions[form.track] || []
})

// 弹窗标题
const title = computed(() => {
  return props.mode === 'add' ? '添加项目' : '编辑项目'
})

// 切换赛道时重置组别
const handleTrackChange = () => {
  // 根据赛道设置默认组别
  const defaultCategories = {
    MAIN_TRACK: 'UNDERGRADUATE_CREATIVE',
    RED_TOURISM_TRACK: 'PUBLIC_WELFARE',
    INDUSTRY_TRACK: 'ENTERPRISE_PROPOSITION'
  }
  form.category = defaultCategories[form.track] || ''
}

// 禁用未来的年份
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 获取项目详情
const fetchProjectDetail = async () => {
  if (!props.projectId) {
    console.warn('没有项目ID')
    return
  }

  try {
    dataLoading.value = true
    const response = await getProjectByIdAPI(props.projectId)
    if (response.success) {
      fillForm(response.data)
    } else {
      ElMessage.error(response.message || '获取项目详情失败')
    }
  } catch (error) {
    console.error('获取项目详情失败:', error)
    ElMessage.error('获取项目详情失败')
  } finally {
    dataLoading.value = false
  }
}

// 填充表单数据
const fillForm = (projectData) => {
  if (!projectData) {
    console.warn('没有项目数据可用于填充表单')
    return
  }

  try {
    Object.keys(form).forEach(key => {
      if (projectData[key] !== undefined && projectData[key] !== null) {
        form[key] = projectData[key]
      }
    })
  } catch (error) {
    console.error('填充表单数据时出错:', error)
    ElMessage.error('填充表单数据失败')
  }
}

// 保存项目
const handleSave = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) {
      ElMessage.error('请完善项目信息')
      return
    }

    loading.value = true

    const submitData = { ...form }
    
    // 如果是编辑模式，添加项目ID
    if (props.mode === 'edit' && props.projectId) {
      submitData.id = props.projectId
    }

    let response
    if (props.mode === 'add') {
      response = await addProjectAPI(submitData)
    } else {
      response = await updateProjectAPI(submitData)
    }

    if (response.success) {
      ElMessage.success(props.mode === 'add' ? '添加项目成功' : '更新项目成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.message || (props.mode === 'add' ? '添加项目失败' : '更新项目失败'))
    }
  } catch (error) {
    console.error('保存项目失败:', error)
    ElMessage.error((props.mode === 'add' ? '添加项目失败: ' : '更新项目失败: ') + error.message)
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
    if (key === 'track') {
      form[key] = 'MAIN_TRACK'
    } else if (key === 'category') {
      form[key] = 'UNDERGRADUATE_CREATIVE'
    } else if (key === 'stage') {
      form[key] = 'SCHOOL'
    } else if (key === 'status') {
      form[key] = 'PREPARING'
    } else if (key === 'projectLevel') {
      form[key] = 'GENERAL'
    } else if (key === 'createdYear') {
      form[key] = new Date().getFullYear().toString()
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
    if (props.mode === 'edit' && props.projectId) {
      nextTick(() => {
        fetchProjectDetail()
      })
    } else {
      // 添加模式下触发赛道改变以设置默认组别
      handleTrackChange()
    }
  }
})

// 监听项目ID变化
watch(() => props.projectId, (newId) => {
  if (newId && dialogVisible.value && props.mode === 'edit') {
    fetchProjectDetail()
  }
})
</script>

<style scoped>
.project-dialog-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 10px 0;
}

.project-form {
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

/* 滚动条样式 */
.project-dialog-container::-webkit-scrollbar {
  width: 6px;
}

.project-dialog-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.project-dialog-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.project-dialog-container::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>