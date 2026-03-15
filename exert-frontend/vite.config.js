import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
      proxy: {
        '/api': { // 请求路径前缀，用于匹配需要代理的请求
          target: 'http://localhost:8080', // 代理的目标后端服务器地址
          secure: false, // 如果是https接口，需要配置这个参数
          changeOrigin: true, // 修改请求头中的Origin字段:cite[2]:cite[3]:cite[6]
          // rewrite: (path) => path.replace(/^\/api/, '') // 将请求路径中的/api前缀去掉
        }
      }
    }
})
