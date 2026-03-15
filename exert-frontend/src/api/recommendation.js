import request from "@/utils/request";

// 获取推荐专家列表
export const getRecommendationListAPI = (data) => request.post('/recommendations', data)

// 获取专家匹配详情
export const getMatchDetailAPI = (params) => request.get('/recommendations/match-detail', {
    params: params || {}
})

// 邀请专家
export const inviteExpertAPI = (data) => request.post('/recommendations/invite', data)

// 批量邀请专家
export const batchInviteExpertsAPI = (data) => request.post('/recommendations/batch-invite', data)

// 获取邀请记录
export const getInvitationHistoryAPI = (params) => request.get('/recommendations/invitations/history', {
    params: params || {}
})

// 获取项目简要列表（用于推荐页面选择项目）
export const getProjectSimpleListAPI = () => request.get('/projects/nameList', {
  params: {
    page: 1,
    pageSize: 1000, // 获取足够多的数据，避免分页问题
    keyWord: ''
  }
})

// 获取推荐权重配置（预留，为后续系统管理模块准备）
export const getRecommendationWeightsAPI = () => request.get('/recommendations/weights')

// 更新推荐权重配置（预留）
export const updateRecommendationWeightsAPI = (data) => request.put('/recommendations/weights', data)

// 获取推荐统计（预留，用于后续数据分析）
export const getRecommendationStatsAPI = (params) => request.get('/recommendations/stats', {
    params: params || {}
})