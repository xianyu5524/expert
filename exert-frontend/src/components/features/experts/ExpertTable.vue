<template>
  <el-table 
    :data="data" 
    stripe 
    v-loading="loading"
    @selection-change="$emit('selection-change', $event)"
  >
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column prop="name" label="姓名" width="100" />
    <el-table-column prop="expertType" label="类型" width="100" />
    <el-table-column prop="field" label="行业领域" width="200">
      <template #default="scope">
        <div class="field-tags">
          <el-tag v-for="field in scope.row.field.split(',')" :key="field.trim()" size="small" class="field-tag">
            {{ field.trim() }}
          </el-tag>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="weight" label="专家权重" width="120">
      <template #default="scope">
        <el-tag :type="getWeightTagType(scope.row.weight)" effect="light">
          {{ getWeightText(scope.row.weight) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="guidanceCount" label="指导次数" width="90" />
    <el-table-column prop="lastInvite" label="最近邀请" width="100" />
    <el-table-column prop="status" label="状态" width="100">
      <template #default="scope">
        <el-tag v-if="scope.row.status === 'APPROVED'" type="success">已通过</el-tag>
        <el-tag v-else-if="scope.row.status === 'PENDING'" type="warning">待审核</el-tag>
        <el-tag v-else type="danger">已拒绝</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="200" fixed="right">
      <template #default="scope">
        <div class="table-actions">
          <el-button size="small" @click="$emit('view', scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="$emit('edit', scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="$emit('delete', scope.row)">删除</el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { getWeightTagType, getWeightText } from '@/utils/formatters'

defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  selectedExperts: {
    type: Array,
    default: () => []
  }
})

defineEmits(['selection-change', 'view', 'edit', 'delete'])
</script>

<style scoped>
.table-actions {
  display: flex;
  gap: 5px;
}

.field-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.field-tag {
  margin-bottom: 4px;
}
</style>