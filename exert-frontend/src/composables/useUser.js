import { ref, reactive } from 'vue'
import { getUserListAPI, addUserAPI, updateUserAPI, deleteUserAPI } from '@/api/user'

export const useUser = (roleType = null) => {
    const loading = ref(false)
    const userList = ref([])
    const pagination = reactive({
        currentPage: 1,
        pageSize: 10,
        total: 0
    })

    // 搜索表单
    const searchForm = reactive({
        username: '',
        name: '',
        role: roleType, // 根据角色类型过滤
        status: ''
    })

    // 获取用户列表
    const fetchUserList = async () => {
        try {
            loading.value = true
            const params = {
                page: pagination.currentPage,
                pageSize: pagination.pageSize,
                ...searchForm
            }

            const response = await getUserListAPI(params)
            if (response.success) {
                userList.value = response.data.list
                pagination.total = response.data.total
            } else {
                console.error('获取用户列表失败:', response.message)
            }
        } catch (error) {
            console.error('获取用户列表失败:', error)
        } finally {
            loading.value = false
        }
    }

    // 搜索
    const handleSearch = async () => {
        pagination.currentPage = 1
        await fetchUserList()
    }

    // 重置搜索
    const handleReset = () => {
        Object.keys(searchForm).forEach(key => {
            if (key !== 'role') { // 保留角色过滤
                searchForm[key] = ''
            }
        })
        pagination.currentPage = 1
        fetchUserList()
    }

    // 分页处理
    const handleSizeChange = (size) => {
        pagination.pageSize = size
        pagination.currentPage = 1
        fetchUserList()
    }

    const handleCurrentChange = (page) => {
        pagination.currentPage = page
        fetchUserList()
    }

    // 初始化获取数据
    fetchUserList()

    return {
        loading,
        userList,
        searchForm,
        pagination,
        fetchUserList,
        handleSearch,
        handleReset,
        handleSizeChange,
        handleCurrentChange
    }
}