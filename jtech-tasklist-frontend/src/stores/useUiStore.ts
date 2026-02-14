import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUiStore = defineStore('ui', () => {
  const snackbar = ref({
    show: false,
    message: '',
    color: 'success'
  })

  const showSnackbar = (message: string, color: string = 'success') => {
    snackbar.value = { show: true, message, color }
    setTimeout(() => {
      snackbar.value.show = false
    }, 3000)
  }

  return {
    snackbar,
    showSnackbar
  }
})
