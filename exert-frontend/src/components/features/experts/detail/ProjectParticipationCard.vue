<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <FolderOpened />
          </el-icon>
          <span>项目参与 ({{ projects?.length || 0 }})</span>
        </div>
      </div>
    </template>

    <div v-if="!projects || projects.length === 0" class="empty-data">
      <el-empty description="暂无项目参与记录" :image-size="80" />
    </div>

    <el-table v-else :data="projects" size="small" stripe class="detail-table">
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="projectName" label="项目名称" min-width="200"></el-table-column>
      <el-table-column prop="role" label="参与角色" width="120"></el-table-column>
      <el-table-column prop="status" label="参与状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ONGOING' ? 'success' : 'warning'" size="small">
            {{ scope.row.status === 'ONGOING' ? '进行中' : '已结题' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目赛道" width="120">
        <template #default="scope">
          <el-tag :type="getTrackTagType(scope.row.track)" size="small">
            {{ formatTrack(scope.row.track) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目组别" width="120">
        <template #default="scope">
          <el-tag size="small" effect="plain">
            {{ formatCategory(scope.row.category) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目阶段" width="100">
        <template #default="scope">
          <el-tag size="small">
            {{ formatProjectStage(scope.row.stage) }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { FolderOpened } from '@element-plus/icons-vue'
import { formatTrack, formatCategory, formatProjectStage, getTrackTagType } from '@/utils/formatters'

defineProps({
  projects: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.empty-data {
  padding: 40px 0;
}
</style>