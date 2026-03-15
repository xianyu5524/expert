<template>
  <el-dialog v-model="dialogVisible" :title="title" width="600px">
    <div class="achievement-form-container">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="成就标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入成就标题" />
        </el-form-item>
        <el-form-item label="获得日期" prop="achievementDate">
          <el-date-picker
            v-model="form.achievementDate"
            type="date"
            placeholder="选择获得日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入成就详细描述"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  form: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    default: -1
  }
})

const emit = defineEmits(['update:modelValue', 'save'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)

const title = computed(() => props.index === -1 ? '添加成就' : '编辑成就')

const rules = {
  title: [{ required: true, message: '请输入成就标题', trigger: 'blur' }],
  achievementDate: [{ required: true, message: '请选择获得日期', trigger: 'change' }]
}

const handleSave = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      emit('save')
    }
  })
}
</script>