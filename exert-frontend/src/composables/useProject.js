import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getPageProjectListAPI, 
  getProjectByIdAPI, 
  addProjectAPI, 
  updateProjectAPI, 
  deleteProjectAPI 
} from '@/api/project'

export function useProject() {
  const loading = ref(false)
  const projectList = ref([])
  const currentProject = ref(null)

  // 搜索表单
  const searchForm = reactive({
    name: '',
    track: '',
    status: '',
    stage: '',
    projectLevel: ''
  })

  // 分页
  const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 0
  })

  // 获取项目列表
  const handleSearch = async () => {
    try {
      loading.value = true
      
      // 构建请求参数
      const params = {
        page: pagination.currentPage,
        pageSize: pagination.pageSize
      }
      
      // 添加搜索条件（非空字段）
      if (searchForm.name) params.name = searchForm.name
      if (searchForm.track) params.track = searchForm.track
      if (searchForm.status) params.status = searchForm.status
      if (searchForm.stage) params.stage = searchForm.stage
      if (searchForm.projectLevel) params.projectLevel = searchForm.projectLevel

      const response = await getPageProjectListAPI(params)
      if (response.success) {
        projectList.value = response.data.records || response.data.list || []
        pagination.total = response.data.total || 0
      } else {
        ElMessage.error(response.message || '获取项目列表失败')
      }
    } catch (error) {
      console.error('获取项目列表失败:', error)
      ElMessage.error('获取项目列表失败')
    } finally {
      loading.value = false
    }
  }

  // 重置搜索
  const handleReset = () => {
    Object.keys(searchForm).forEach(key => {
      searchForm[key] = ''
    })
    pagination.currentPage = 1
    handleSearch()
  }

  // 分页大小改变
  const handleSizeChange = (size) => {
    pagination.pageSize = size
    pagination.currentPage = 1
    handleSearch()
  }

  // 当前页改变
  const handleCurrentChange = (page) => {
    pagination.currentPage = page
    handleSearch()
  }

  // 获取项目详情
  const fetchProjectDetail = async (projectId) => {
    try {
      loading.value = true
      const response = await getProjectByIdAPI(projectId)
      if (response.success) {
        currentProject.value = response.data
        return response.data
      } else {
        ElMessage.error(response.message || '获取项目详情失败')
        return null
      }
    } catch (error) {
      console.error('获取项目详情失败:', error)
      ElMessage.error('获取项目详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 删除项目
  const deleteProject = async (projectIds) => {
    try {
      loading.value = true
      const response = await deleteProjectAPI(projectIds)
      if (response.success) {
        ElMessage.success('删除项目成功')
        return true
      } else {
        ElMessage.error(response.message || '删除项目失败')
        return false
      }
    } catch (error) {
      console.error('删除项目失败:', error)
      ElMessage.error('删除项目失败')
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    projectList,
    currentProject,
    searchForm,
    pagination,
    handleSearch,
    handleReset,
    handleSizeChange,
    handleCurrentChange,
    fetchProjectDetail,
    deleteProject
  }
}