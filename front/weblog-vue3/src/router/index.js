import { createRouter, createWebHashHistory } from 'vue-router';

import Index from '@/pages/frontend/index.vue';
import ArchiveList from '@/pages/frontend/archive-list.vue';
import CategoryList from '@/pages/frontend/category-list.vue';
import CategoryArticleList from '@/pages/frontend/category-article-list.vue';
import TagList from '@/pages/frontend/tag-list.vue';
import TagArticleList from '@/pages/frontend/tag-article-list.vue';
import ArticleDetail from '@/pages/frontend/article-detail.vue';
import NotFound from '@/pages/frontend/404.vue';
import Login from '@/pages/admin/login.vue';
import AdminIndex from '@/pages/admin/index.vue';
import AdminArticleList from '@/pages/admin/article-list.vue';
import AdminCategoryList from '@/pages/admin/category-list.vue';
import AdminTagList from '@/pages/admin/tag-list.vue';
import AdminBlogSettings from '@/pages/admin/blog-settings.vue';
import Admin from '@/layouts/admin/admin.vue';
import AiRobotIndex from '@/pages/ai-robot/index.vue';
import AiRobotChat from '@/pages/ai-robot/chat.vue';
import AdminKnowledgeBase from '@/pages/admin/knowledge-base.vue';

const routes = [
  {
    path: '/',
    component: Index,
    meta: { title: 'Weblog 首页' },
  },
  {
    path: '/archive/list',
    component: ArchiveList,
    meta: { title: 'Weblog 归档页' },
  },
  {
    path: '/category/list',
    component: CategoryList,
    meta: { title: 'Weblog 分类列表页' },
  },
  {
    path: '/category/article/list',
    component: CategoryArticleList,
    meta: { title: 'Weblog 分类文章页' },
  },
  {
    path: '/tag/list',
    component: TagList,
    meta: { title: 'Weblog 标签列表页' },
  },
  {
    path: '/tag/article/list',
    component: TagArticleList,
    meta: { title: 'Weblog 标签文章页' },
  },
  {
    path: '/article/:articleId',
    component: ArticleDetail,
    meta: { title: 'Weblog 详情页' },
  },
  {
    path: '/ai-robot',
    name: 'AiRobotIndex',
    component: AiRobotIndex,
    meta: { title: 'AI 机器人' },
  },
  {
    path: '/ai-robot/chat/:chatId',
    name: 'AiRobotChatPage',
    component: AiRobotChat,
    meta: { title: 'AI 聊天' },
  },
  {
    path: '/login',
    component: Login,
    meta: { title: 'Weblog 登录页' },
  },
  {
    path: '/admin',
    component: Admin,
    children: [
      {
        path: '/admin/index',
        component: AdminIndex,
        meta: { title: '仪表盘' },
      },
      {
        path: '/admin/article/list',
        component: AdminArticleList,
        meta: { title: '文章管理' },
      },
      {
        path: '/admin/category/list',
        component: AdminCategoryList,
        meta: { title: '分类管理' },
      },
      {
        path: '/admin/tag/list',
        component: AdminTagList,
        meta: { title: '标签管理' },
      },
      {
        path: '/admin/blog/settings',
        component: AdminBlogSettings,
        meta: { title: '博客设置' },
      },
      {
        path: '/admin/knowledge/base',
        component: AdminKnowledgeBase,
        meta: { title: '知识库管理' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '404 页面' },
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
