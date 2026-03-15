<template>
  <div class="step-content">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="basic-form">
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入专家姓名" size="large" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="MALE">男</el-radio>
                <el-radio value="FEMALE">女</el-radio>
                <el-radio value="OTHER">其他</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="选择出生日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专家类型" prop="expertType">
              <el-select v-model="form.expertType" placeholder="请选择专家类型" style="width: 100%">
                <el-option label="教育专家" value="EDUCATION" />
                <el-option label="企业专家" value="ENTERPRISE" />
              </el-select>
            </el-form-item>
            <el-form-item label="专家权重" prop="weight">
              <el-select v-model="form.weight" placeholder="请选择专家权重" style="width: 100%">
                <el-option label="一级" :value="1" />
                <el-option label="二级" :value="2" />
                <el-option label="三级" :value="3" />
                <el-option label="四级" :value="4" />
                <el-option label="五级" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待审核" value="PENDING" />
                <el-option label="已通过" value="APPROVED" />
                <el-option label="已拒绝" value="REJECTED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">工作信息</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入工作单位" />
            </el-form-item>
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="教授" value="教授" />
                <el-option label="副教授" value="副教授" />
                <el-option label="讲师" value="讲师" />
                <el-option label="研究员" value="研究员" />
                <el-option label="副研究员" value="副研究员" />
                <el-option label="高级工程师" value="高级工程师" />
                <el-option label="工程师" value="工程师" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 银行卡信息 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="银行卡号" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行卡号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开户行" prop="bankName">
              <el-select
                v-model="form.bankName"
                placeholder="请选择开户行"
                filterable
                allow-create
                style="width: 100%"
              >
                <el-option v-for="bank in bankOptions" :key="bank" :label="bank" :value="bank" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">联系信息</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="form-section">
        <h3 class="section-title">其他信息</h3>
        <el-form-item label="行业领域" prop="field">
          <el-input
            v-model="form.field"
            placeholder="请输入行业领域，多个领域用逗号分隔"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="个人简介" prop="introduction">
          <el-input
            v-model="form.introduction"
            placeholder="请输入专家个人简介"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  form: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  mode: {
    type: String,
    default: 'add' // 'add' 或 'edit'
  }
})

// 暴露验证方法给父组件
defineExpose({
  validate: () => formRef.value?.validate()
})

const formRef = ref(null)

// 银行选项
const bankOptions = [
  '中国工商银行', '中国农业银行', '中国银行', '中国建设银行',
  '交通银行', '招商银行', '中国邮政储蓄银行', '中信银行',
  '中国光大银行', '华夏银行', '中国民生银行', '广发银行',
  '平安银行', '兴业银行', '浦发银行', '浙商银行'
]
</script>

<style scoped>
.step-content {
  min-height: 400px;
}

.form-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background: #409EFF;
  margin-right: 8px;
  border-radius: 2px;
}
</style>