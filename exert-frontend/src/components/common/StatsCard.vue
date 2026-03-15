<template>
  <el-card class="stats-card">
    <div class="stats-number">{{ number }}</div>
    <div class="stats-label">{{ label }}</div>
  </el-card>
</template>

<script setup>
defineProps({
  number: {
    type: [Number, String],
    default: 0
  },
  label: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.stats-card {
  text-align: center;
  padding: 20px 0;
}

.stats-number {
  font-size: 2rem;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.stats-label {
  color: #666;
}
</style>