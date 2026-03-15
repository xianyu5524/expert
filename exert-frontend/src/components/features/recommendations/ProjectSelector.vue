<template>
  <div class="project-selector">
    <el-card class="selector-card">
      <template #header>
        <div class="card-header">
          <span>选择项目</span>
          <el-button 
            type="primary" 
            link 
            @click="handleViewAllProjects"
          >
            查看所有项目
          </el-button>
        </div>
      </template>
      
      <el-form :model="form" label-width="80px">
        <el-form-item label="项目选择">
          <el-select
            v-model="form.selectedProjectId"
            placeholder="请选择项目"
            filterable
            clearable
            style="width: 100%"
            @change="handleProjectSelect"
            :loading="loading"
          >
            <el-option
              v-for="project in projectList"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            >
              <div class="project-option">
                <span class="project-name">{{ project.name }}</span>
                <span class="project-meta">
                  {{ formatTrack(project.track) }} · {{ formatProjectStage(project.stage) }}
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <!-- 筛选条件配置 -->
        <el-form-item label="筛选条件" v-if="selectedProject">
          <div class="filter-config">
            <div class="filter-add">
              <el-select
                v-model="newFilterKey"
                placeholder="选择筛选条件"
                style="width: 140px; margin-right: 10px;"
              >
                <el-option
                  v-for="option in filterOptions"
                  :key="option.key"
                  :label="option.label"
                  :value="option.key"
                />
              </el-select>
              
              <el-select
                v-model="newFilterValue"
                placeholder="选择值"
                style="width: 120px; margin-right: 10px;"
                :disabled="!newFilterKey"
              >
                <el-option
                  v-for="value in getFilterValues(newFilterKey)"
                  :key="value"
                  :label="value"
                  :value="value"
                />
              </el-select>
              
              <el-button 
                type="primary" 
                @click="addFilter"
                :disabled="!newFilterKey || !newFilterValue"
              >
                添加
              </el-button>
            </div>
            
            <div class="filter-tags">
              <el-tag 
                v-for="filter in activeFilters" 
                :key="filter.key"
                closable
                @close="removeFilter(filter.key)"
                type="primary"
              >
                {{ filter.label }}: {{ filter.value }}
              </el-tag>
              <el-text v-if="activeFilters.length === 0" type="info" size="small">
                暂无筛选条件
              </el-text>
            </div>
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 选中的项目信息 -->
      <div v-if="selectedProject" class="project-info">
        <h4>项目信息</h4>
        <el-descriptions :column="2" size="small" border>
          <el-descriptions-item label="项目名称">
            {{ selectedProject.name }}
          </el-descriptions-item>
          <el-descriptions-item label="项目赛道">
            <el-tag :type="getTrackTagType(selectedProject.track)" size="small">
              {{ formatTrack(selectedProject.track) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目分类">
            {{ formatCategory(selectedProject.category) }}
          </el-descriptions-item>
          <el-descriptions-item label="项目阶段">
            <el-tag :type="getStageTagType(selectedProject.stage)" size="small">
              {{ formatProjectStage(selectedProject.stage) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目负责人">
            {{ selectedProject.leaderName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建年份">
            {{ selectedProject.createdYear || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="项目状态">
            <el-tag :type="getStatusTagType(selectedProject.status)" size="small">
              {{ formatProjectStatus(selectedProject.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目等级" v-if="selectedProject.projectLevel">
            <el-tag :type="getLevelTagType(selectedProject.projectLevel)" size="small">
              {{ formatProjectLevel(selectedProject.projectLevel) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

// API导入
import { getProjectSimpleListAPI } from '@/api/recommendation'

// 导入格式化函数
import {
  formatTrack,
  formatProjectStage,
  formatCategory,
  formatProjectStatus,
  formatProjectLevel,
  getTrackTagType,
  getStageTagType,
  getStatusTagType,
  getLevelTagType
} from '@/utils/formatters'

const router = useRouter()
const emit = defineEmits(['projectChange', 'filtersChange'])

// 响应式数据
const projectList = ref([])
const selectedProject = ref(null)
const loading = ref(false)
const form = reactive({
  selectedProjectId: null
})

// 筛选条件相关
const activeFilters = ref([])
const newFilterKey = ref('')
const newFilterValue = ref('')

// 可用的筛选选项
const filterOptions = ref([
  { key: 'matchField', label: '领域匹配度', values: ['高', '中', '低'] },
  { key: 'experience', label: '项目经验', values: ['丰富', '一般', '较少'] },
  { key: 'availability', label: '专家可用性', values: ['高', '中', '低'] },
  { key: 'successRate', label: '历史成功率', values: ['高', '中', '低'] },
  { key: 'responseTime', label: '响应速度', values: ['快', '中', '慢'] }
])

// 方法定义
const loadProjectList = async () => {
  loading.value = true
  try {
    const response = await getProjectSimpleListAPI()
    // 根据后端返回结构调整，这里假设返回的是数组或包含list字段的对象
    const rawData = response.data?.list || response.data?.data?.list || response.data || []
    
    // 对项目列表数据进行格式化处理
    projectList.value = rawData.map(project => ({
      ...project,
      // 确保所有可能为空的字段都有默认值
      track: project.track || '',
      stage: project.stage || '',
      category: project.category || '',
      status: project.status || '',
      projectLevel: project.projectLevel || '',
      leaderName: project.leaderName || '',
      createdYear: project.createdYear || ''
    }))
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
    projectList.value = []
  } finally {
    loading.value = false
  }
}

const handleProjectSelect = (projectId) => {
  const project = projectList.value.find(p => p.id === projectId)
  selectedProject.value = project
  emit('projectChange', project)
}

const handleViewAllProjects = () => {
  router.push('/projects')
}

// 筛选条件相关方法
const getFilterValues = (key) => {
  const option = filterOptions.value.find(opt => opt.key === key)
  return option ? option.values : []
}

const addFilter = () => {
  if (!newFilterKey.value || !newFilterValue.value) return
  
  // 检查是否已存在相同的筛选条件
  const exists = activeFilters.value.some(filter => filter.key === newFilterKey.value)
  if (exists) {
    ElMessage.warning('该筛选条件已存在')
    return
  }
  
  const filterOption = filterOptions.value.find(opt => opt.key === newFilterKey.value)
  activeFilters.value.push({
    key: newFilterKey.value,
    label: filterOption.label,
    value: newFilterValue.value
  })
  
  // 重置输入
  newFilterKey.value = ''
  newFilterValue.value = ''
  
  // 通知父组件筛选条件变化
  emit('filtersChange', activeFilters.value)
}

const removeFilter = (filterKey) => {
  activeFilters.value = activeFilters.value.filter(f => f.key !== filterKey)
  // 通知父组件筛选条件变化
  emit('filtersChange', activeFilters.value)
}

onMounted(() => {
  loadProjectList()
})
</script>

<style scoped>
.project-selector {
  margin-bottom: 20px;
}

.selector-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.project-option {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.project-name {
  font-weight: 500;
}

.project-meta {
  font-size: 12px;
  color: #909399;
}

.filter-config {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-add {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  min-height: 32px;
  align-items: center;
}

.project-info {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.project-info h4 {
  margin: 0 0 12px 0;
  color: #303133;
}
</style>