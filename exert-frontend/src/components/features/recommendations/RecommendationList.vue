<template>
  <div class="recommendation-list">
    <!-- 列表头部 -->
    <div class="list-header">
      <div class="header-info">
        <h3>推荐专家 ({{ recommendations.length }})</h3>
        <span class="tip-text">根据项目需求智能匹配的专家列表</span>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          :disabled="selectedExperts.length === 0"
          @click="handleBatchInvite"
        >
          批量邀请 ({{ selectedExperts.length }})
        </el-button>
      </div>
    </div>

    <!-- 专家列表 -->
    <div class="expert-list">
      <div 
        v-for="recommendation in recommendations" 
        :key="recommendation.expert.id"
        class="expert-card"
        :class="{ selected: isExpertSelected(recommendation.expert.id) }"
      >
        <ExpertCard 
          :recommendation="recommendation"
          @view-detail="$emit('view-detail', recommendation.expert)"
          @invite-expert="$emit('invite-expert', recommendation.expert)"
          @select-change="handleSelectChange"
        />
      </div>
    </div>

    <!-- 分页器 -->
    <div class="pagination-container" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 组件导入
import ExpertCard from './ExpertCard.vue'

const props = defineProps({
  recommendations: {
    type: Array,
    default: () => []
  },
  selectedProject: {
    type: Object,
    default: null
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  selectedExperts: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits([
  'view-detail', 
  'invite-expert', 
  'batch-invite',
  'size-change',
  'current-change',
  'select-change'
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => props.recommendations.length)

// 计算当前页显示的数据
const paginatedRecommendations = computed(() => {
  const start = (props.currentPage - 1) * props.pageSize
  const end = start + props.pageSize
  return props.recommendations.slice(start, end)
})

// 添加 isExpertSelected 函数
const isExpertSelected = (expertId) => {
  return props.selectedExperts.some(expert => expert.id === expertId)
}

// 方法定义
const handleBatchInvite = () => {
  if (props.selectedExperts.length > 0) {
    emit('batch-invite', props.selectedExperts)
  }
}

const handleSizeChange = (newSize) => {
  emit('size-change', newSize)
}

const handleCurrentChange = (newPage) => {
  emit('current-change', newPage)
}

const handleSelectChange = (expert, selected) => {
  emit('select-change', expert, selected)
}
</script>

<style scoped>
.recommendation-list {
  background: white;
  border-radius: 8px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.header-info h3 {
  margin: 0 0 4px 0;
  color: #303133;
}

.tip-text {
  font-size: 12px;
  color: #909399;
}

.expert-list {
  padding: 0;
}

.expert-card {
  border-bottom: 1px solid #f0f2f5;
  transition: background-color 0.2s;
}

.expert-card:last-child {
  border-bottom: none;
}

.expert-card.selected {
  background-color: #f0f7ff;
}

.pagination-container {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
}
</style>