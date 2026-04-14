import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/admin/user'
import { removeToken } from '@/composables/cookie'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({ roles: [] })

  // 设置用户信息
  function setUserInfo() {
    // 调用后端获取用户信息接口
    return getUserInfo().then(res => {
      if (res.success === true) {
        userInfo.value = res.data || { roles: [] }
      }
      return res
    })
  }

  // 退出登录
  function logout() {
    removeToken()
    userInfo.value = { roles: [] }
  }

  return { userInfo, setUserInfo, logout }
},
{
  // 开启持久化
  persist: true,
}
)
