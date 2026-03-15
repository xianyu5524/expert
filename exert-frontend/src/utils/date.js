// 日期时间格式化
export const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return '-'
    
    // 如果已经是格式化好的字符串，直接返回
    if (dateTimeStr.includes(' ') && dateTimeStr.includes('-')) {
        return dateTimeStr
    }
    
    // 处理 ISO 格式的日期字符串
    try {
        const date = new Date(dateTimeStr)
        if (isNaN(date.getTime())) return '-'
        
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        
        return `${year}-${month}-${day} ${hours}:${minutes}`
    } catch (error) {
        console.error('日期格式化错误:', error)
        return dateTimeStr.replace('T', ' ').substring(0, 16) // 回退到简单处理
    }
}

// 日期格式化
export const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    
    try {
        const date = new Date(dateStr)
        if (isNaN(date.getTime())) return '-'
        
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        
        return `${year}-${month}-${day}`
    } catch (error) {
        console.error('日期格式化错误:', error)
        return dateStr.split('T')[0] // 回退到简单处理
    }
}