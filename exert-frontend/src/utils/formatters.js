// 日期格式化
export const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

export const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.replace('T', ' ').substring(0, 16)
}

// 字段处理
export const getFieldArray = (fieldStr) => {
  if (!fieldStr) return []
  return fieldStr.split(',').map(field => field.trim()).filter(field => field)
}

// 获取权重标签类型
export const getWeightTagType = (weight) => {
  switch (weight) {
    case 1: return 'info'
    case 2: return ''
    case 3: return 'warning'
    case 4: return 'success'
    case 5: return 'danger'
    default: return 'info'
  }
}

// 获取权重文本
export const getWeightText = (weight) => {
  switch (weight) {
    case 1: return '一级'
    case 2: return '二级'
    case 3: return '三级'
    case 4: return '四级'
    case 5: return '五级'
    default: return '未分级'
  }
}

// 格式化专家参与状态
export const formatExpertStatus = (status) => {
  const statusMap = {
    'ONGOING': '参与中',
    'COMPLETED': '已结束'
  }
  return statusMap[status] || status
}

// 赛道格式化
export const formatTrack = (track) => {
  const trackMap = {
    'MAIN_TRACK': '高教主赛道',
    'RED_TOURISM_TRACK': '红旅赛道',
    'INDUSTRY_TRACK': '产业赛道'
  }
  return trackMap[track] || track
}

export const getTrackTagType = (track) => {
  const typeMap = {
    'MAIN_TRACK': 'success',
    'RED_TOURISM_TRACK': 'danger',
    'INDUSTRY_TRACK': 'warning'
  }
  return typeMap[track] || 'info'
}

export const getInvitationStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 项目阶段格式化
export const formatProjectStage = (stage) => {
  const stageMap = {
    'SCHOOL': '校赛',
    'PROVINCIAL': '省赛',
    'NATIONAL': '国赛'
  }
  return stageMap[stage] || stage
}

// 获取项目阶段标签类型
export const getStageTagType = (stage) => {
  const typeMap = {
    'SCHOOL': 'info',
    'PROVINCIAL': 'warning',
    'NATIONAL': 'success'
  }
  return typeMap[stage] || 'info'
}

// 项目类别格式化
export const formatCategory = (category) => {
  const categoryMap = {
    // 高教主赛道
    'UNDERGRADUATE_CREATIVE': '本科生创意组',
    'UNDERGRADUATE_ENTREPRENEURIAL': '本科生创业组',
    'POSTGRADUATE_CREATIVE': '研究生创意组',
    'POSTGRADUATE_ENTREPRENEURIAL': '研究生创业组',
    // 红旅赛道
    'PUBLIC_WELFARE': '公益组',
    'RED_CREATIVE': '创意组',
    'RED_ENTREPRENEURIAL': '创业组',
    // 产业赛道
    'ENTERPRISE_PROPOSITION': '企业命题组',
    'REGIONAL_INDUSTRY': '区域特色产业组',
    'ACHIEVEMENT_TRANSFORMATION': '成果转化组'
  }
  return categoryMap[category] || category
}

export const formatInvitationStatus = (status) => {
  const statusMap = {
    'PENDING': '待响应',
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

// ========== 项目管理新增格式化函数 ==========

// 项目状态格式化
export const formatProjectStatus = (status) => {
  const statusMap = {
    'PREPARING': '准备中',
    'ONGOING': '进行中',
    'SUSPENDED': '已暂停',
    'COMPLETED': '已完成',
    'TERMINATED': '已终止'
  }
  return statusMap[status] || status
}

// 获取项目状态标签类型
export const getStatusTagType = (status) => {
  const typeMap = {
    'PREPARING': 'info',
    'ONGOING': 'success',
    'SUSPENDED': 'warning',
    'COMPLETED': '',
    'TERMINATED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 奖项格式化
export const formatAward = (award) => {
  const awardMap = {
    'FIRST_PRIZE': '一等奖',
    'SECOND_PRIZE': '二等奖',
    'THIRD_PRIZE': '三等奖'
  }
  return awardMap[award] || award
}

// 项目等级格式化
export const formatProjectLevel = (level) => {
  const levelMap = {
    'KEY': '重点',
    'GENERAL': '一般',
    'OTHER': '其他'
  }
  return levelMap[level] || level
}

// 获取项目等级标签类型
export const getLevelTagType = (level) => {
  const typeMap = {
    'KEY': 'danger',
    'GENERAL': 'primary',
    'OTHER': 'info'
  }
  return typeMap[level] || 'info'
}

// 用户角色格式化
export const formatUserRole = (role) => {
  const roleMap = {
    'SUPER_ADMIN': '超管',
    'ADMIN': '管理员',
    'EXPERT': '专家',
    'OTHER': '其他'
  }
  return roleMap[role] || role
}

// 用户状态格式化
export const formatUserStatus = (status) => {
  const statusMap = {
    'ACTIVE': '活跃',
    'INACTIVE': '禁用',
    'PENDING': '待激活'
  }
  return statusMap[status] || status
}

// 角色标签类型
export const getUserRoleTagType = (role) => {
  const typeMap = {
    'SUPER_ADMIN': 'danger',
    'ADMIN': 'warning',
    'EXPERT': 'success',
    'OTHER': 'info'
  }
  return typeMap[role] || 'info'
}

// 状态标签类型
export const getUserStatusTagType = (status) => {
  const typeMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'danger',
    'PENDING': 'warning'
  }
  return typeMap[status] || 'info'
}