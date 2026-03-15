import request from "@/utils/request";


export const getExpertListAPI = (queryParams) => request.get('/experts', {
    params: {
        page: queryParams?.page || 1,      // 页码
        pageSize: queryParams?.size || 20,     // 每页大小
        name: queryParams?.name,           // 专家姓名筛选
        status: queryParams?.status,       // 状态筛选
        expertType: queryParams?.expertType,// 专家类型筛选
        weight: queryParams?.weight        // 权重筛选
    }
})

export const getExpertDetailAPI = (id) => request.get(`/experts/${id}`)

export const addExpertAPI = (data) => request.post('/experts', data)

export const updateExpertAPI = (data) => request.put('/experts', data)

export const deleteExpertsAPI = (ids) => request.delete('/experts', {
    data: ids
})

// 专家批量操作相关
export const downloadExpertTemplateAPI = () => request.get('/experts/template', {
    responseType: 'blob'
})

export const exportExpertsAPI = (params) => request.get('/experts/export', {
    params: params,
    responseType: 'blob'
})

export const importExpertsAPI = (formData) => request.post('/experts/import', formData, {
    headers: {
        'Content-Type': 'multipart/form-data'
    }
})