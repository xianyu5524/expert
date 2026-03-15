import axios from 'axios'

//创建axios实例对象
const request = axios.create({
    baseURL: '/api',
    timeout: 60000
})

// 添加请求拦截器
request.interceptors.request.use(
    (config) => {
        // 在发送请求之前做些什么
        // 从本地存储（localStorage）获取token :cite[6]
        const token = localStorage.getItem('userToken'); // 确保键名与登录时存储的一致

        // 如果token存在，将其添加到请求头中 :cite[6]
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 注意：常见的格式是 "Bearer [token]"
        }
        return config;
    },
    (error) => {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 添加响应拦截器
request.interceptors.response.use(
    (response) => {// 对响应数据做点什么

        // 对于blob响应，直接返回整个response
        if (response.config.responseType === 'blob') {
            return response
        }

        // 对于普通JSON响应，检查success字段
        if (response.data && response.data.success) {
            return response.data
        } else {
            return Promise.reject(new Error(response.data.message || '请求失败'))
        }
    },
    (error) => {// 对响应错误做点什么

        // 对于blob请求的错误处理
        if (error.config?.responseType === 'blob' && error.response?.data) {
            // 尝试读取blob错误信息
            return new Promise((resolve, reject) => {
                const reader = new FileReader()
                reader.onload = () => {
                    try {
                        const errorData = JSON.parse(reader.result)
                        reject(new Error(errorData.message || '导出失败'))
                    } catch {
                        reject(new Error('导出失败'))
                    }
                }
                reader.readAsText(error.response.data)
            })
        }
        return Promise.reject(error)
    }
)

export default request