import { ref,reactive  } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjectListAPI } from '@/api/project'

export function useProjectSelection() {
  const projectOptions = ref([])
  const projectLoading = ref(false)
  const projectSearchQuery = ref('')
  const projectPagination = reactive({
    page: 1,
    pageSize: 20,
    total: 0
  })

  // 加载项目列表
  const loadAvailableProjects = async (params = {}) => {
    try {
      projectLoading.value = true
      
      const requestParams = {
        page: params.page || projectPagination.page,
        pageSize: params.pageSize || projectPagination.pageSize,
        ...params
      }
      
      if (projectSearchQuery.value) {
        requestParams.keyWord = projectSearchQuery.value
      }

      const response = await getProjectListAPI(requestParams)
      if (response.success && response.code === 200) {
        projectOptions.value = response.data.list || response.data || []
        projectPagination.total = response.data.total || 0
        
        // 更新分页信息
        if (params.page) projectPagination.page = params.page
        if (params.pageSize) projectPagination.pageSize = params.pageSize
        
        return response.data
      }
      return null
    } catch (error) {
      console.error('加载项目列表失败:', error)
      ElMessage.error('加载项目列表失败')
      projectOptions.value = []
      return null
    } finally {
      projectLoading.value = false
    }
  }

  // 搜索项目
  const handleProjectSearch = (query) => {
    projectSearchQuery.value = query
    projectPagination.page = 1
    loadAvailableProjects()
  }

  // 重置搜索
  const resetProjectSearch = () => {
    projectSearchQuery.value = ''
    projectPagination.page = 1
    loadAvailableProjects()
  }

  return {
    projectOptions,
    projectLoading,
    projectSearchQuery,
    projectPagination,
    loadAvailableProjects,
    handleProjectSearch,
    resetProjectSearch
  }
}