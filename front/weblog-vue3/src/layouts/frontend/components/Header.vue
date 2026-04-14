<template>
  <header class="sticky top-0 z-50 w-full">
    <nav
      class="page-shell relative flex items-center justify-between py-4 sm:py-4 px-4 sm:px-0 transition-all duration-300 rounded-full bg-white/90 text-slate-900 border border-slate-200 shadow-lg shadow-slate-900/5 backdrop-blur supports-[backdrop-filter]:backdrop-blur dark:bg-slate-950/90 dark:text-white dark:border-slate-800/80 dark:shadow-black/20">
      <a href="/" class="flex items-center gap-3 group">
        <div
          class="relative w-12 h-12 overflow-hidden rounded-2xl border border-white/40 shadow-lg bg-white/15 backdrop-blur">
          <img :src="blogSettingsStore.blogSettings.logo" class="w-full h-full object-cover" alt="Logo" />
        </div>
        <div>
          <p class="text-lg font-semibold text-slate-900 dark:text-white tracking-tight group-hover:text-indigo-500 dark:group-hover:text-indigo-300 transition-colors">
            {{ blogSettingsStore.blogSettings.name }}
          </p>
          <p class="text-xs text-slate-500 dark:text-slate-200">Notes, code and a bit of sunlight</p>
        </div>
      </a>

      <div
        class="hidden md:flex items-center gap-1 px-2 py-1 rounded-full border shadow-sm bg-white border-slate-200 text-slate-700 dark:bg-white/10 dark:border-white/15 backdrop-blur-md">
        <button v-for="(item, index) in navItems" :key="index" @click="router.push(item.path)"
          class="px-4 py-2 text-sm font-medium rounded-full transition-all duration-200"
          :class="[isActiveNav(item.path) ? 'bg-slate-900 text-white shadow dark:bg-white dark:text-slate-900' : 'text-slate-700 hover:bg-slate-900/5 dark:text-white/80 dark:hover:bg-white/10']">
          {{ item.name }}
        </button>
      </div>

      <div class="flex items-center gap-2 sm:gap-3">
        <div class="hidden md:flex items-center px-3 py-2 rounded-full border shadow-sm bg-white text-slate-600 border-slate-200 dark:bg-white/10 dark:text-white/70 dark:border-white/15">
          <svg class="w-4 h-4 text-slate-500 dark:text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input type="text"
            class="w-36 focus:w-52 ml-2 transition-all duration-300 text-sm bg-transparent border-none focus:ring-0 text-slate-700 placeholder-slate-400 dark:text-white dark:placeholder-white/60"
            placeholder="搜索文章" />
        </div>

        <button @click="router.push('/ai-robot')"
          class="hidden md:inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white text-slate-900 text-sm font-semibold shadow-lg shadow-indigo-500/15 hover:-translate-y-0.5 transition">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6l4 2" />
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"></circle>
          </svg>
          AI 机器人
        </button>

        <div v-if="!isLogined">
          <button @click="$router.push('/login')"
            class="px-4 py-2 text-sm font-semibold text-slate-900 bg-white rounded-full shadow hover:bg-slate-100 transition">
            登录
          </button>
        </div>

        <div v-else class="relative group">
          <button class="flex items-center gap-2 focus:outline-none rounded-full px-2 py-1 border bg-white/10 border-white/40 text-white">
            <img class="w-9 h-9 rounded-full shadow-md object-cover" :src="blogSettingsStore.blogSettings.avatar"
              alt="user photo">
            <svg class="w-4 h-4 text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
          <div
            class="absolute right-0 mt-2 w-48 bg-white dark:bg-slate-900 rounded-xl shadow-xl py-2 invisible opacity-0 group-hover:visible group-hover:opacity-100 transition-all duration-200 transform origin-top-right border border-slate-100 dark:border-slate-800 text-slate-800">
            <a v-if="isAdmin" @click="router.push('/admin/index')"
              class="hidden md:block px-4 py-2 text-sm text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800 cursor-pointer">
              <span class="flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z">
                  </path>
                </svg>后台管理</span>
            </a>
            <a @click="logout"
              class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-slate-800 cursor-pointer">
              <span class="flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1">
                  </path>
                </svg>退出登录</span>
            </a>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { showMessage } from '@/composables/util'

const router = useRouter()
const route = useRoute()
const currPath = ref(route.path)
const blogSettingsStore = useBlogSettingsStore()
const userStore = useUserStore()
const isLogined = computed(() => !!userStore.userInfo.username)
const isAdmin = computed(() => (userStore.userInfo.roles || []).includes('ROLE_ADMIN'))
const isScrolled = ref(false)

const navItems = [
  { name: '首页', path: '/' },
  { name: '分类', path: '/category/list' },
  { name: '标签', path: '/tag/list' },
  { name: '归档', path: '/archive/list' },
  { name: 'AI 机器人', path: '/ai-robot' },
]

const isActiveNav = (path) => currPath.value === path || currPath.value.startsWith(path + '/')

onMounted(() => {
  window.addEventListener('scroll', () => {
    isScrolled.value = window.scrollY > 20
  })
})

watch(() => route.path, (newPath) => {
  currPath.value = newPath
})

const logout = () => {
  userStore.logout()
  showMessage('退出登录成功')
  router.replace('/')
}
</script>
