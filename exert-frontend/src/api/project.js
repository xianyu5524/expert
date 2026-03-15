import request from "@/utils/request";

// 获取项目名称列表（用于专家管理模块）
export const getProjectListAPI = (params) => request.get('/projects/nameList', { 
    params: params || {}
})

// 添加项目
export const addProjectAPI = (data) => request.post('/projects', data)

// 分页获取项目列表（用于项目管理模块）
export const getPageProjectListAPI = (params) => request.get('/projects/pageList', {
    params: params || {}
})

// 根据ID获取项目详情
export const getProjectByIdAPI = (id) => request.get(`/projects/${id}`)

// 更新项目
export const updateProjectAPI = (data) => request.put('/projects', data)

// 删除项目
export const deleteProjectAPI = (ids) => request.delete(`/projects`, {
    data: ids
})

// 项目批量操作相关
export const downloadProjectTemplateAPI = () => request.get('/projects/template', {
    responseType: 'blob'
})

export const exportProjectsAPI = (params) => request.get('/projects/export', {
    params: params,
    responseType: 'blob'
})

export const importProjectsAPI = (formData) => request.post('/projects/import', formData, {
    headers: {
        'Content-Type': 'multipart/form-data'
    }
})