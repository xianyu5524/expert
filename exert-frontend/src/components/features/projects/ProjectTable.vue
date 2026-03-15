<template>
  <el-table 
    :data="data" 
    stripe 
    v-loading="loading"
    @selection-change="$emit('selection-change', $event)"
  >
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column prop="name" label="项目名称" min-width="200" />
    <el-table-column prop="track" label="项目赛道" width="120">
      <template #default="scope">
        <el-tag :type="getTrackTagType(scope.row.track)" size="small">
          {{ formatTrack(scope.row.track) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="category" label="项目组别" width="140">
      <template #default="scope">
        <el-tag size="small" effect="plain">
          {{ formatCategory(scope.row.category) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="leaderName" label="负责人" width="100" />
    <el-table-column prop="grade" label="年级" width="80" />
    <el-table-column prop="major" label="专业" width="120" />
    <el-table-column prop="stage" label="项目阶段" width="100">
      <template #default="scope">
        <el-tag :type="getStageTagType(scope.row.stage)" size="small">
          {{ formatProjectStage(scope.row.stage) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="status" label="项目状态" width="100">
      <template #default="scope">
        <el-tag :type="getStatusTagType(scope.row.status)" size="small">
          {{ formatProjectStatus(scope.row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="award" label="获奖情况" width="100">
      <template #default="scope">
        <span v-if="scope.row.award">{{ formatAward(scope.row.award) }}</span>
        <span v-else>-</span>
      </template>
    </el-table-column>
    <el-table-column prop="advisor" label="指导老师" width="100" />
    <el-table-column prop="projectLevel" label="项目等级" width="100">
      <template #default="scope">
        <el-tag :type="getLevelTagType(scope.row.projectLevel)" size="small">
          {{ formatProjectLevel(scope.row.projectLevel) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="createdYear" label="创建年份" width="100" />
    <el-table-column label="操作" width="240" fixed="right">
      <template #default="scope">
        <div class="table-actions">
          <el-button size="small" type="primary" @click="$emit('edit', scope.row)">编辑</el-button>
          <el-button size="small" type="success" @click="$emit('invite-expert', scope.row)">邀请专家</el-button>
          <el-button size="small" type="danger" @click="$emit('delete', scope.row)">删除</el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { 
  formatTrack, 
  formatCategory, 
  getTrackTagType, 
  formatProjectStage, 
  formatProjectStatus, 
  formatAward, 
  formatProjectLevel,
  getStageTagType,
  getStatusTagType,
  getLevelTagType
} from '@/utils/formatters'


defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['selection-change', 'edit', 'delete', 'invite-expert'])
</script>