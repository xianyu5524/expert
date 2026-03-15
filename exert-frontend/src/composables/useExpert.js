import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getExpertListAPI, getExpertDetailAPI, deleteExpertsAPI } from '@/api/expert'

export function useExpert() {
  const loading = ref(false)
  const expertList = ref([])
  const currentExpert = ref(null)

  const searchForm = reactive({
    name: '',
    status: '',
    expertType: '',
    weight: null
  })

  const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 0
  })

  // 获取专家列表
  const fetchExpertList = async () => {
    loading.value = true
    try {
      const response = await getExpertListAPI({
        name: searchForm.name,
        status: searchForm.status,
        expertType: searchForm.expertType,
        weight: searchForm.weight,
        page: pagination.currentPage,
        pageSize: pagination.pageSize
      })

      expertList.value = response.data.list || []
      pagination.total = response.data.total || 0
    } catch (error) {
      console.error('请求失败:', error)
      ElMessage.error('获取专家列表失败')
    } finally {
      loading.value = false
    }
  }

  // 获取专家详情
  const fetchExpertDetail = async (expertId) => {
    loading.value = true
    try {
      const response = await getExpertDetailAPI(expertId)
      currentExpert.value = response.data
      return response.data
    } catch (error) {
      console.error('获取专家详情失败:', error)
      ElMessage.error('获取专家详情失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 搜索和重置
  const handleSearch = async () => {
    pagination.currentPage = 1
    await fetchExpertList()
  }

  const handleReset = () => {
    Object.keys(searchForm).forEach(key => {
      searchForm[key] = ''
    })
    handleSearch()
  }

  // 分页处理
  const handleSizeChange = (size) => {
    pagination.pageSize = size
    handleSearch()
  }

  const handleCurrentChange = (page) => {
    pagination.currentPage = page
    handleSearch()
  }

  // 删除专家
  const deleteExpert = async (ids) => {
    try {
      loading.value = true
      const response = await deleteExpertsAPI(ids)
      // 根据删除数量显示不同消息
      if (response.success) {
        // 删除成功后重置到第一页
        pagination.currentPage = 1
        
        if (ids.length === 1) {
          ElMessage.success('删除专家成功')
        } else {
          ElMessage.success(`成功删除 ${ids.length} 位专家`)
        }
        return true
      }

    } catch (error) {
      if (ids.length === 1) {
        ElMessage.error('删除专家失败')
      } else {
        ElMessage.error('批量删除专家失败')
      }
      console.error('删除失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    expertList,
    currentExpert,
    searchForm,
    pagination,
    fetchExpertList,
    fetchExpertDetail,
    handleSearch,
    handleReset,
    handleSizeChange,
    handleCurrentChange,
    deleteExpert
  }
}