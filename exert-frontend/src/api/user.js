import request from '@/utils/request'

export const getUserListAPI = (params) => request.get('/users/pageList', {
    params: params || {}
})

export const getUserByIdAPI = (userId) => request.get(`/users/${userId}`)

export const addUserAPI = (data) => request.post('/users', data)

export const updateUserAPI = (data) => request.put('/users', data)

export const deleteUserAPI = (ids) => request.delete(`/users`, {
    data: ids
})

export const toggleUserStatusAPI = (userId, status) => request.patch(`/users/${userId}/status?status=${status}`)

export const resetPasswordAPI = (userId) => request.post(`/users/${userId}/reset-password`)