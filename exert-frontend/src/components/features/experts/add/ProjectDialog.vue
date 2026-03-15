<template>
  <el-dialog v-model="dialogVisible" title="添加项目参与" width="700px" @close="handleClose">
    <div class="project-participation-container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <!-- 项目选择部分 -->
        <el-form-item label="项目选择" prop="projectOption">
          <el-radio-group v-model="form.projectOption" @change="handleProjectOptionChange">
            <el-radio label="existing">选择现有项目</el-radio>
            <el-radio label="new">创建新项目</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 现有项目选择 -->
        <div v-if="form.projectOption === 'existing'">
          <el-form-item label="选择项目" prop="projectId">
            <el-select
              v-model="form.projectId"
              placeholder="请输入关键词搜索项目"
              style="width: 100%"
              filterable
              remote
              clearable
              :remote-method="handleProjectSearch"
              :loading="projectLoading"
              @focus="loadAvailableProjects"
            >
              <el-option
                v-for="project in projectOptions"
                :key="project.id"
                :label="project.name"
                :value="project.id"
              >
                <div class="project-option">
                  <span class="project-name">{{ project.name }}</span>
                  <span class="project-info">
                    <el-tag size="small" :type="getTrackTagType(project.track)">
                      {{ formatTrack(project.track) }}
                    </el-tag>
                    <el-tag size="small" effect="plain" style="margin-left: 5px;">
                      {{ formatCategory(project.category) }}
                    </el-tag>
                  </span>
                </div>
              </el-option>

              <!-- 分页提示 -->
              <el-option disabled v-if="projectOptions.length > 0">
                <div class="pagination-hint">
                  已显示 {{ projectOptions.length }} 个项目
                  <span v-if="projectPagination.total > projectOptions.length">
                    ，共 {{ projectPagination.total }} 个项目
                  </span>
                </div>
              </el-option>

              <el-option disabled v-if="projectOptions.length === 0 && !projectLoading">
                <div class="empty-hint">暂无项目数据</div>
              </el-option>
            </el-select>
          </el-form-item>
        </div>

        <!-- 新建项目表单 -->
        <div v-if="form.projectOption === 'new'">
          <el-divider content-position="left">新建项目信息</el-divider>

          <el-form-item label="项目名称" prop="newProject.name">
            <el-input v-model="form.newProject.name" placeholder="请输入项目名称" />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="项目赛道" prop="newProject.track">
                <el-select 
                  v-model="form.newProject.track" 
                  placeholder="选择项目赛道" 
                  style="width: 100%"
                  @change="handleTrackChange"
                >
                  <el-option 
                    v-for="track in trackOptions" 
                    :key="track.value" 
                    :label="track.label" 
                    :value="track.value" 
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目组别" prop="newProject.category">
                <el-select 
                  v-model="form.newProject.category" 
                  placeholder="先选择赛道" 
                  style="width: 100%"
                  :disabled="!form.newProject.track"
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
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="负责人" prop="newProject.leaderName">
                <el-input v-model="form.newProject.leaderName" placeholder="请输入负责人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年级">
                <el-input v-model="form.newProject.grade" placeholder="请输入年级" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="专业">
                <el-input v-model="form.newProject.major" placeholder="请输入专业" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目阶段" prop="newProject.stage">
                <el-select v-model="form.newProject.stage" placeholder="选择项目阶段" style="width: 100%">
                  <el-option label="校赛" value="SCHOOL" />
                  <el-option label="省赛" value="PROVINCIAL" />
                  <el-option label="国赛" value="NATIONAL" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="项目状态" prop="newProject.status">
                <el-select v-model="form.newProject.status" placeholder="选择项目状态" style="width: 100%">
                  <el-option label="准备中" value="PREPARING" />
                  <el-option label="进行中" value="ONGOING" />
                  <el-option label="已暂停" value="SUSPENDED" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="已终止" value="TERMINATED" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目等级">
                <el-select v-model="form.newProject.projectLevel" placeholder="选择项目等级" style="width: 100%">
                  <el-option label="重点" value="KEY" />
                  <el-option label="一般" value="GENERAL" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="奖项">
                <el-select v-model="form.newProject.award" placeholder="选择奖项" style="width: 100%" clearable>
                  <el-option label="一等奖" value="FIRST_PRIZE" />
                  <el-option label="二等奖" value="SECOND_PRIZE" />
                  <el-option label="三等奖" value="THIRD_PRIZE" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="指导老师">
                <el-input v-model="form.newProject.advisor" placeholder="请输入指导老师" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="创建年份">
            <el-date-picker
              v-model="form.newProject.createdYear"
              type="year"
              placeholder="选择年份"
              style="width: 100%"
              value-format="YYYY"
            />
          </el-form-item>
        </div>

        <!-- 专家参与信息 -->
        <el-divider content-position="left">专家参与信息</el-divider>

        <el-form-item label="参与角色" prop="role">
          <el-input
            v-model="form.role"
            placeholder="请输入专家在项目中的角色，如：评审专家、技术指导、顾问等"
          />
        </el-form-item>

        <el-form-item label="参与状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ONGOING">进行中</el-radio>
            <el-radio label="COMPLETED">已结题</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          确认添加
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useProjectSelection } from '@/composables/useProjectSelection'
import { formatTrack, formatCategory, getTrackTagType } from '@/utils/formatters'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'add' // 'add' 或 'edit'
  }
})

const emit = defineEmits(['update:modelValue', 'save', 'close'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)
const loading = ref(false)

// 使用项目选择 composable
const {
  projectOptions,
  projectLoading,
  projectSearchQuery,
  projectPagination,
  loadAvailableProjects,
  handleProjectSearch
} = useProjectSelection()

// 赛道选项
const trackOptions = [
  { label: '高教主赛道', value: 'MAIN_TRACK' },
  { label: '红旅赛道', value: 'RED_TOURISM_TRACK' },
  { label: '产业赛道', value: 'INDUSTRY_TRACK' }
]

// 组别选项（根据赛道动态变化）
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
  if (!form.newProject.track) return []
  return categoryOptions[form.newProject.track] || []
})

// 表单数据
const form = reactive({
  projectOption: 'existing',
  projectId: null,
  role: '',
  status: 'ONGOING',
  newProject: {
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
  }
})

// 表单验证规则
const rules = {
  projectOption: [
    { required: true, message: '请选择项目选项', trigger: 'change' }
  ],
  projectId: [
    {
      required: true,
      message: '请选择项目',
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'existing' && !value) {
          callback(new Error('请选择项目'))
        } else {
          callback()
        }
      }
    }
  ],
  role: [
    { required: true, message: '请输入参与角色', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择参与状态', trigger: 'change' }
  ],
  'newProject.name': [
    {
      required: true,
      message: '请输入项目名称',
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请输入项目名称'))
        } else {
          callback()
        }
      }
    }
  ],
  'newProject.track': [
    {
      required: true,
      message: '请选择项目赛道',
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请选择项目赛道'))
        } else {
          callback()
        }
      }
    }
  ],
  'newProject.category': [
    {
      required: true,
      message: '请选择项目组别',
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请选择项目组别'))
        } else {
          callback()
        }
      }
    }
  ],
  'newProject.leaderName': [
    {
      required: true,
      message: '请输入负责人姓名',
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请输入负责人姓名'))
        } else {
          callback()
        }
      }
    }
  ],
  'newProject.stage': [
    {
      required: true,
      message: '请选择项目阶段',
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请选择项目阶段'))
        } else {
          callback()
        }
      }
    }
  ],
  'newProject.status': [
    {
      required: true,
      message: '请选择项目状态',
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (form.projectOption === 'new' && !value) {
          callback(new Error('请选择项目状态'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 切换赛道时重置组别
const handleTrackChange = () => {
  // 根据赛道设置默认组别
  const defaultCategories = {
    MAIN_TRACK: 'UNDERGRADUATE_CREATIVE',
    RED_TOURISM_TRACK: 'PUBLIC_WELFARE',
    INDUSTRY_TRACK: 'ENTERPRISE_PROPOSITION'
  }
  form.newProject.category = defaultCategories[form.newProject.track] || ''
}

// 项目选项改变处理
const handleProjectOptionChange = (value) => {
  if (value === 'existing') {
    loadAvailableProjects()
  } else {
    // 切换到新建项目时，触发赛道改变以设置默认组别
    handleTrackChange()
  }
}

// 保存项目
const handleSave = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请完善项目信息')
      return
    }

    try {
      loading.value = true

      let projectData = {
        role: form.role,
        status: form.status,
      }

      if (form.projectOption === 'existing') {
        const selectedProject = projectOptions.value.find(
          p => p.id === form.projectId
        )
        if (!selectedProject) {
          ElMessage.error('未找到选择的项目')
          return
        }

        projectData = {
          ...projectData,
          projectId: selectedProject.id,
          projectName: selectedProject.name,
          projectTrack: selectedProject.track,
          projectCategory: selectedProject.category,
          isNewProject: false
        }
      } else {
        projectData = {
          ...projectData,
          projectId: null,
          projectName: form.newProject.name,
          projectTrack: form.newProject.track,
          projectCategory: form.newProject.category,
          isNewProject: true,
          newProjectData: {
            name: form.newProject.name,
            track: form.newProject.track,
            category: form.newProject.category,
            leaderName: form.newProject.leaderName,
            grade: form.newProject.grade,
            major: form.newProject.major,
            stage: form.newProject.stage,
            status: form.newProject.status,
            award: form.newProject.award,
            advisor: form.newProject.advisor,
            projectLevel: form.newProject.projectLevel,
            createdYear: form.newProject.createdYear
          }
        }
      }

      emit('save', projectData)
      ElMessage.success('项目参与信息已添加')
      handleClose()
    } catch (error) {
      console.error('保存项目信息失败:', error)
      ElMessage.error('保存项目信息失败')
    } finally {
      loading.value = false
    }
  })
}

// 关闭对话框
const handleClose = () => {
  resetForm()
  emit('close')
  dialogVisible.value = false
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    projectOption: 'existing',
    projectId: null,
    role: '',
    status: 'ONGOING',
    newProject: {
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
    if (form.projectOption === 'existing') {
      loadAvailableProjects()
    }
  }
})
</script>

<style scoped>
.project-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.project-name {
  flex: 1;
  margin-right: 10px;
}

.project-info {
  display: flex;
  align-items: center;
}

.pagination-hint {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 8px 0;
}

.empty-hint {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 8px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>