<template>
  <div class="step-content">
    <el-card shadow="never" class="review-card">
      <template #header>
        <h3 class="review-title">信息确认</h3>
      </template>

      <div class="review-section">
        <h4>基本信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ form.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ form.gender === 'MALE' ? '男' : form.gender === 'FEMALE' ? '女' : '其他' }}
          </el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ form.birthDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专家类型">
            {{ form.expertType === 'EDUCATION' ? '教育专家' : '企业专家' }}
          </el-descriptions-item>
          <el-descriptions-item label="专家权重">{{ form.weight }}级</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
              :type="form.status === 'APPROVED' ? 'success' : form.status === 'PENDING' ? 'warning' : 'danger'"
            >
              {{ form.status === 'APPROVED' ? '已通过' : form.status === 'PENDING' ? '待审核' : '已拒绝' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="review-section">
        <h4>工作信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="单位">{{ form.unit }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ form.department || '-' }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ form.title }}</el-descriptions-item>
          <el-descriptions-item label="银行卡号">{{ form.bankAccount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开户行">{{ form.bankName || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="review-section">
        <h4>联系信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="联系方式">{{ form.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ form.email }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="review-section">
        <h4>其他信息</h4>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="行业领域">
            <div class="field-tags">
              <el-tag v-for="(field, index) in getFieldArray(form.field)" :key="index" size="small" class="field-tag">
                {{ field }}
              </el-tag>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="个人简介">
            <div class="introduction-content">{{ form.introduction || '无' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div v-if="form.achievements.length > 0" class="review-section">
        <h4>个人成就 ({{ form.achievements.length }}项)</h4>
        <div v-for="(achievement, index) in form.achievements" :key="index" class="review-item">
          <div class="achievement-review">
            <el-tag type="info" size="small">{{ index + 1 }}</el-tag>
            <div class="achievement-details">
              <div class="item-title">{{ achievement.title }}</div>
              <div class="item-date">{{ achievement.achievementDate }}</div>
              <div v-if="achievement.description" class="item-description">{{ achievement.description }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="form.expertProjects.length > 0" class="review-section">
        <h4>项目参与 ({{ form.expertProjects.length }}项)</h4>
        <div v-for="(project, index) in form.expertProjects" :key="index" class="review-item">
          <div class="project-review">
            <el-tag type="info" size="small">{{ index + 1 }}</el-tag>
            <div class="project-details">
              <div class="item-title">{{ getProjectName(project) }}</div>
              <div class="item-role">{{ project.role }}</div>
              <el-tag :type="project.status === 'ONGOING' ? 'success' : 'warning'" size="small">
                {{ project.status === 'ONGOING' ? '进行中' : '已结题' }}
              </el-tag>
              <div v-if="project.isNewProject" style="margin-top: 4px;">
                <el-tag type="success" size="small">新建项目</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { getFieldArray } from '@/utils/formatters'

defineProps({
  form: {
    type: Object,
    required: true
  },
  projectOptions: {
    type: Array,
    default: () => []
  },
  mode: {
    type: String,
    default: 'add'
  }
})

// 获取项目名称
const getProjectName = (project) => {
  if (project.isNewProject) {
    return project.projectName || '新项目'
  } else {
    // 在实际项目中，这里应该根据 projectId 从 projectOptions 中查找项目名称
    return project.projectName || '现有项目'
  }
}
</script>

<style scoped>
.step-content {
  min-height: 400px;
}

.review-section {
  margin-bottom: 24px;
}

.review-section h4 {
  margin-bottom: 12px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.achievement-review,
.project-review {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.achievement-details,
.project-details {
  margin-left: 12px;
  flex: 1;
}

.item-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.item-date,
.item-role {
  color: #606266;
  font-size: 14px;
  margin-bottom: 4px;
}

.item-description {
  color: #909399;
  font-size: 14px;
  line-height: 1.4;
}

.introduction-content {
  line-height: 1.6;
  color: #606266;
}

.field-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.field-tag {
  margin: 2px;
}
</style>