<template>
  <div class="step-content">
    <!-- 成就信息 -->
    <el-card class="detail-card modern-card" shadow="hover">
      <template #header>
        <div class="card-header modern-header">
          <div class="header-title">
            <el-icon>
              <Trophy />
            </el-icon>
            <span>个人成就 ({{ form.achievements.length }})</span>
          </div>
          <el-button type="primary" size="small" @click="$emit('add-achievement')">
            <el-icon>
              <Plus />
            </el-icon>
            添加成就
          </el-button>
        </div>
      </template>

      <!-- 成就表格 -->
      <el-table :data="form.achievements" size="small" stripe class="detail-table">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="title" label="成就标题" min-width="200"></el-table-column>
        <el-table-column prop="achievementDate" label="获得日期" width="150">
          <template #default="scope">
            {{ scope.row.achievementDate ? formatDate(scope.row.achievementDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="详细描述" min-width="250" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button size="small" type="primary" @click="$emit('edit-achievement', scope.$index)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="$emit('remove-achievement', scope.$index)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="form.achievements.length === 0" class="empty-data">
        <el-empty description="暂无成就信息" :image-size="80" />
      </div>
    </el-card>

    <!-- 项目参与卡片 -->
    <el-card class="detail-card modern-card" shadow="hover">
      <template #header>
        <div class="card-header modern-header">
          <div class="header-title">
            <el-icon>
              <FolderOpened />
            </el-icon>
            <span>项目参与 ({{ form.expertProjects.length }})</span>
          </div>
          <el-button type="primary" size="small" @click="$emit('add-project')">
            <el-icon>
              <Plus />
            </el-icon>
            添加项目
          </el-button>
        </div>
      </template>

      <el-table :data="form.expertProjects" size="small" stripe class="detail-table">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="role" label="参与角色" width="120"></el-table-column>
        <el-table-column prop="status" label="参与状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ONGOING' ? 'success' : 'warning'" size="small">
              {{ scope.row.status === 'ONGOING' ? '进行中' : '已结题' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="项目类型" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isNewProject" type="success" size="small">新建</el-tag>
            <el-tag v-else type="primary" size="small">现有</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" text @click="$emit('remove-project', scope.row)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="form.expertProjects.length === 0" class="empty-data">
        <el-empty description="暂无项目参与记录" :image-size="80" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Trophy, Plus, FolderOpened } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/formatters'

defineProps({
  form: {
    type: Object,
    required: true
  },
  mode: {
    type: String,
    default: 'add'
  }
})

defineEmits(['add-achievement', 'edit-achievement', 'remove-achievement', 'add-project', 'remove-project'])
</script>

<style scoped>
.step-content {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-card {
  border-radius: 8px;
}

.modern-card {
  border: none;
  transition: all 0.3s ease;
}

.modern-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modern-header {
  background: linear-gradient(90deg, #f8f9fa, #ffffff);
  border-radius: 8px 8px 0 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.table-actions {
  display: flex;
  gap: 5px;
}

.empty-data {
  padding: 40px 0;
}
</style>