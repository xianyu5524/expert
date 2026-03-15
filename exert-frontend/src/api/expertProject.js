import request from "@/utils/request";

export const addExpertProjectAPI = (data) => request.post('/expert-projects', data)

export const getExpertProjectsAPI = (expertId) => request.get(`/expert-projects/${expertId}`, expertId)