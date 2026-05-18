import { ElMessage, ElMessageBox } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

export const can = (permission?: string) => useAdminStore().hasPermission(permission)

export const requireText = (value: string | undefined, label: string) => {
  if (!value || !value.trim()) {
    ElMessage.warning(`${label}必填`)
    return false
  }
  return true
}

export const promptReason = async (title: string, placeholder = '请输入操作原因') => {
  const result = await ElMessageBox.prompt(placeholder, title, {
    inputType: 'textarea',
    inputPattern: /\S+/,
    inputErrorMessage: '操作原因必填',
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  return result.value.trim()
}

export const confirmCritical = (message: string, title = '关键操作确认') => ElMessageBox.confirm(message, title, {
  type: 'warning',
  confirmButtonText: '确认执行',
  cancelButtonText: '取消'
})
