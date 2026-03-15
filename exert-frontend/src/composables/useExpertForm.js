import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

export function useExpertForm() {
  const currentStep = ref(0)
  const loading = ref(false)
  const basicFormRef = ref(null)

  // 表单数据
  const form = reactive({
    // 基本信息
    name: '',
    gender: 'MALE',
    birthDate: '',
    expertType: 'EDUCATION',
    weight: 3,
    status: 'PENDING',
    // 工作信息
    unit: '',
    department: '',
    title: '',
    bankAccount: '',
    bankName: '',
    // 联系信息
    phone: '',
    email: '',
    // 其他信息
    field: '',
    introduction: '',
    // 动态添加的部分
    achievements: [],
    expertProjects: []
  })

  // 表单验证规则
  const rules = {
    name: [{ required: true, message: '请输入专家姓名', trigger: 'blur' }],
    gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
    unit: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
    title: [{ required: true, message: '请选择职称', trigger: 'change' }],
    expertType: [{ required: true, message: '请选择专家类型', trigger: 'change' }],
    weight: [{ required: true, message: '请选择专家权重', trigger: 'change' }],
    phone: [
      { required: true, message: '请输入联系方式', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    field: [{ required: true, message: '请输入行业领域', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
    bankName: [{ required: true, message: '请选择开户行', trigger: 'change' }],
    bankAccount: [
      { required: true, message: '请输入银行卡号', trigger: 'blur' },
      { pattern: /^[1-9]\d{9,18}$/, message: '银行卡号格式不正确', trigger: 'blur' }
    ]
  }

  // 成就弹窗相关
  const achievementDialogVisible = ref(false)
  const achievementForm = reactive({
    title: '',
    achievementDate: '',
    description: ''
  })
  const currentAchievementIndex = ref(-1)

  // 项目弹窗相关
  const projectDialogVisible = ref(false)

  // 账号信息弹窗
  const accountInfoDialog = reactive({
    visible: false,
    username: '',
    name: ''
  })

  // 步骤控制
  const handleNextStep = async () => {
    if (currentStep.value === 0) {
      if (!basicFormRef.value) return
      try {
        await basicFormRef.value.validate()
        currentStep.value++
      } catch (error) {
        ElMessage.error('请填写完整的基本信息')
      }
    } else {
      currentStep.value++
    }
  }

  const handlePrevStep = () => {
    if (currentStep.value > 0) {
      currentStep.value--
    }
  }

  // 成就管理
  const handleAddAchievement = () => {
    currentAchievementIndex.value = -1
    achievementForm.title = ''
    achievementForm.achievementDate = ''
    achievementForm.description = ''
    achievementDialogVisible.value = true
  }

  const handleEditAchievement = (index) => {
    currentAchievementIndex.value = index
    const achievement = form.achievements[index]
    achievementForm.title = achievement.title
    achievementForm.achievementDate = achievement.achievementDate
    achievementForm.description = achievement.description
    achievementDialogVisible.value = true
  }

  const removeAchievement = (index) => {
    form.achievements.splice(index, 1)
    ElMessage.success('成就已删除')
  }

  const handleSaveAchievement = () => {
    if (currentAchievementIndex.value === -1) {
      // 新增
      form.achievements.push({ ...achievementForm })
    } else {
      // 编辑
      form.achievements[currentAchievementIndex.value] = { ...achievementForm }
    }
    achievementDialogVisible.value = false
    ElMessage.success('保存成功')
  }

  // 项目管理
  const handleAddProject = () => {
    projectDialogVisible.value = true
  }

  const handleRemoveProject = (project) => {
    const index = form.expertProjects.findIndex(p => p.projectId === project.projectId)
    if (index !== -1) {
      form.expertProjects.splice(index, 1)
      ElMessage.success('项目参与记录已移除')
    }
  }

  const handleSaveProject = (projectData) => {
    form.expertProjects.push(projectData)
    projectDialogVisible.value = false
  }

  const resetForm = () => {
    currentStep.value = 0
    Object.keys(form).forEach(key => {
      if (Array.isArray(form[key])) {
        form[key] = []
      } else {
        form[key] = ''
      }
    })
    // 重置默认值
    form.gender = 'MALE'
    form.expertType = 'EDUCATION'
    form.weight = 3
    form.status = 'PENDING'
  }

  return {
    form,
    rules,
    currentStep,
    loading,
    basicFormRef,
    achievementDialogVisible,
    projectDialogVisible,
    currentAchievementIndex,
    achievementForm,
    accountInfoDialog,
    handleNextStep,
    handlePrevStep,
    handleAddAchievement,
    handleEditAchievement,
    removeAchievement,
    handleAddProject,
    handleRemoveProject,
    handleSaveAchievement,
    handleSaveProject,
    resetForm
  }
}