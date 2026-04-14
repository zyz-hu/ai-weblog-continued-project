import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage, showPageLoading, hidePageLoading } from '@/composables/util'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useUserStore } from '@/stores/user'

const BRAND_NAME = '元智 AI 博客'
const LEGACY_SITE_NAME = 'Weblog'
const FRONTEND_TITLE_MAP = {
    '/': '首页',
    '/archive/list': '归档',
    '/category/list': '分类',
    '/category/article/list': '分类文章',
    '/tag/list': '标签',
    '/tag/article/list': '标签文章',
    '/ai-robot': 'AI 机器人',
}

function getFrontendPageTitle(path) {
    if (path.startsWith('/article/')) {
        return '文章详情'
    }

    if (path.startsWith('/ai-robot/chat/')) {
        return 'AI 对话'
    }

    return FRONTEND_TITLE_MAP[path]
}

// 全局路由前置守卫
router.beforeEach(async (to, from, next) => {
    showPageLoading()

    const token = getToken()
    const userStore = useUserStore()

    if (!token && to.path.startsWith('/admin')) {
        showMessage('请先登录', 'warning')
        next({ path: '/login' })
        return
    }

    if (token && (!userStore.userInfo.roles || userStore.userInfo.roles.length === 0)) {
        await userStore.setUserInfo().catch(() => {})
    }
    const isAdmin = userStore.userInfo.roles && userStore.userInfo.roles.includes('ROLE_ADMIN')

    if (token && to.path.startsWith('/admin') && !isAdmin) {
        showMessage('无权限访问后台', 'error')
        next({ path: '/' })
        return
    }

    if (token && to.path === '/login') {
        showMessage('请勿重复登录', 'warning')
        next({ path: '/admin/index' })
        return
    }

    if (!to.path.startsWith('/admin')) {
        const blogSettingsStore = useBlogSettingsStore()
        blogSettingsStore.getBlogSettings()
    }

    next()
})

// 全局路由后置守卫
router.afterEach((to) => {
    const frontendPageTitle = getFrontendPageTitle(to.path)

    if (frontendPageTitle) {
        document.title = `${BRAND_NAME} | ${frontendPageTitle}`
    } else if (to.meta.title) {
        document.title = `${to.meta.title} - ${LEGACY_SITE_NAME}`
    } else {
        document.title = LEGACY_SITE_NAME
    }

    hidePageLoading()
})
