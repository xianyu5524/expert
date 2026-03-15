// utils/batchService.js
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'
import { ElMessage, ElLoading } from 'element-plus'
import { 
  downloadExpertTemplateAPI, 
  exportExpertsAPI, 
  importExpertsAPI 
} from '@/api/expert'
import { 
  downloadProjectTemplateAPI, 
  exportProjectsAPI, 
  importProjectsAPI 
} from '@/api/project'

class BatchService {
  /**
   * 下载模板
   */
  async downloadTemplate(type, templateName) {
    try {
      let response
      if (type === 'expert') {
        response = await downloadExpertTemplateAPI()
      } else if (type === 'project') {
        response = await downloadProjectTemplateAPI()
      } else {
        throw new Error('不支持的类型')
      }
      
      const blob = new Blob([response.data], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      })
      saveAs(blob, `${templateName}_模板.xlsx`)
      ElMessage.success('模板下载成功')
    } catch (error) {
      console.error('下载模板失败:', error)
      ElMessage.error('模板下载失败')
    }
  }

  /**
   * 导出数据
   */
  async exportData(type, params = {}, fileName) {
    try {
      const loading = ElLoading.service({
        lock: true,
        text: '正在导出数据...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      let response
      if (type === 'experts') {
        response = await exportExpertsAPI(params)
      } else if (type === 'projects') {
        response = await exportProjectsAPI(params)
      } else {
        throw new Error('不支持的类型')
      }

      const blob = new Blob([response.data], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      })
      saveAs(blob, `${fileName}_${new Date().getTime()}.xlsx`)
      
      loading.close()
      ElMessage.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
      ElMessage.error('导出失败')
    }
  }

  /**
   * 导入数据
   */
  async importData(type, file, onProgress) {
    try {
      onProgress?.(10)
      
      const formData = new FormData()
      formData.append('file', file)
      
      onProgress?.(30)
      
      let response
      if (type === 'expert') {
        response = await importExpertsAPI(formData)
      } else if (type === 'project') {
        response = await importProjectsAPI(formData)
      } else {
        throw new Error('不支持的类型')
      }

      onProgress?.(100)
      return response
      
    } catch (error) {
      console.error('导入失败:', error)
      throw new Error(error.message || '导入失败')
    }
  }

  /**
   * 读取 Excel 文件（用于前端预览）
   */
  async readExcelFile(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      
      reader.onload = (e) => {
        try {
          const data = new Uint8Array(e.target.result)
          const workbook = XLSX.read(data, { type: 'array' })
          resolve(workbook)
        } catch (error) {
          reject(error)
        }
      }
      
      reader.onerror = () => reject(new Error('文件读取失败'))
      reader.readAsArrayBuffer(file)
    })
  }
}

export default new BatchService()