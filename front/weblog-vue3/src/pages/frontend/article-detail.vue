<template>
    <Header></Header>

    <section class="relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900"></div>
        <div class="absolute top-0 left-1/3 w-80 h-80 bg-indigo-500/30 blur-[140px] rounded-full"></div>
        <div class="absolute -bottom-10 right-10 w-96 h-96 bg-emerald-400/30 blur-[150px] rounded-full"></div>
        <div class="page-shell relative z-10 py-14 space-y-4">
            <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/12 border border-white/25 text-white text-xs">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
                {{ article.categoryName || '未分类' }}
            </div>
            <h1 class="text-3xl sm:text-4xl font-extrabold text-white leading-tight">{{ article.title }}</h1>
            <div class="flex flex-wrap items-center gap-4 text-white/80 text-sm">
                <span class="inline-flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z">
                        </path>
                    </svg>
                    {{ article.createTime }}
                </span>
                <span class="inline-flex items-center gap-1">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z">
                        </path>
                    </svg>
                    {{ article.readNum }}
                </span>
            </div>
            <div v-if="article.tags && article.tags.length" class="flex flex-wrap gap-2 pt-2">
                <span v-for="(tag, index) in article.tags" :key="index"
                    @click="goTagArticleListPage(tag.id, tag.name)"
                    class="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/15 text-white text-xs cursor-pointer hover:bg-white/25 transition">
                    # {{ tag.name }}
                </span>
            </div>
        </div>
    </section>

    <main class="page-shell -mt-10 pb-16 relative z-10">
        <div class="grid grid-cols-12 gap-8">
            <div class="col-span-12 lg:col-span-8 xl:col-span-9 space-y-6">
                <div class="glass-card p-6 md:p-8 space-y-6">
                    <div v-if="article.cover" class="overflow-hidden rounded-2xl border front-border">
                        <img :src="article.cover" class="w-full h-auto object-cover" />
                    </div>
                    <div ref="articleContentRef" class="article-content" v-viewer v-html="article.content"></div>
                    <div class="flex flex-col md:flex-row gap-4">
                        <a v-if="article.preArticle" @click="router.push('/article/' + article.preArticle.articleId)"
                            class="group glass-card front-surface front-hover-surface p-4 border cursor-pointer flex-1">
                            <p class="text-xs front-text-soft mb-1">上一篇</p>
                            <p class="font-semibold front-text group-hover:text-indigo-600 line-clamp-2">
                                {{ article.preArticle.articleTitle }}
                            </p>
                        </a>
                        <a v-if="article.nextArticle" @click="router.push('/article/' + article.nextArticle.articleId)"
                            class="group glass-card front-surface front-hover-surface p-4 border cursor-pointer flex-1">
                            <p class="text-xs front-text-soft mb-1 text-right">下一篇</p>
                            <p class="font-semibold front-text text-right group-hover:text-indigo-600 line-clamp-2">
                                {{ article.nextArticle.articleTitle }}
                            </p>
                        </a>
                    </div>
                </div>
            </div>

            <aside class="col-span-12 lg:col-span-4 xl:col-span-3 space-y-4">
                <UserInfoCard></UserInfoCard>
                <CategoryListCard></CategoryListCard>
                <TagListCard></TagListCard>
                <Toc></Toc>
            </aside>
        </div>
    </main>

    <ScrollToTopButton></ScrollToTopButton>
    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue';
import Footer from '@/layouts/frontend/components/Footer.vue';
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue';
import TagListCard from '@/layouts/frontend/components/TagListCard.vue';
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue';
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue';
import Toc from '@/layouts/frontend/components/Toc.vue';
import { getArticleDetail } from '@/api/frontend/article';
import { useRoute, useRouter } from 'vue-router';
import { ref, watch, onMounted } from 'vue';
import hljs from 'highlight.js';
import 'highlight.js/styles/tokyo-night-dark.css';

const route = useRoute();
const router = useRouter();
const article = ref({});

function refreshArticleDetail(articleId) {
    getArticleDetail(articleId).then((res) => {
        if (!res.success && res.errorCode == '20010') {
            router.push({ name: 'NotFound' });
            return;
        }
        article.value = res.data;
    });
}
refreshArticleDetail(route.params.articleId);

const goCategoryArticleListPage = (id, name) => {
    router.push({ path: '/category/article/list', query: { id, name } });
};

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/article/list', query: { id, name } });
};

watch(route, (newRoute) => {
    refreshArticleDetail(newRoute.params.articleId);
});

const articleContentRef = ref(null);
onMounted(() => {
    const observer = new MutationObserver(mutationsList => {
        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                let highlight = document.querySelectorAll('pre code');
                highlight.forEach((block) => {
                    hljs.highlightElement(block);
                });
            }
        }
    });
    const config = { childList: true, subtree: true };
    observer.observe(articleContentRef.value, config);
});
</script>

<style>
.article-content {
    color: var(--front-text-muted);
    line-height: 1.8;
}

.article-content h1,
.article-content h2,
.article-content h3,
.article-content h4,
.article-content h5,
.article-content h6 {
    color: var(--front-text);
    font-weight: 700;
    line-height: 1.4;
    margin-top: 32px;
    margin-bottom: 18px;
}

.article-content h2 {
    font-size: 26px;
    border-bottom: 1px solid var(--front-border-strong);
    padding-bottom: 12px;
}

.article-content h3 {
    font-size: 22px;
}

.article-content h4 {
    font-size: 18px;
}

.article-content p {
    margin: 0 0 18px;
    color: var(--front-text-muted);
}

.article-content strong,
.article-content b {
    color: var(--front-text);
}

.article-content blockquote {
    border-left: 3px solid #22c55e;
    background: var(--front-surface-soft);
    padding: 16px 18px;
    margin: 18px 0;
    color: var(--front-text);
    border-radius: 12px;
}

.article-content em {
    color: #6366f1;
}

.article-content a {
    color: #0ea5e9;
    text-decoration: underline;
}

.article-content ul {
    padding-left: 1.5rem;
}

.article-content ul li,
.article-content ol li {
    padding: 6px 0;
    color: var(--front-text-muted);
}

.article-content ol {
    padding-left: 1.5rem;
}

.article-content img {
    max-width: 100%;
    border-radius: 14px;
    margin: 18px auto;
    display: block;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.08);
}

.article-content code:not(pre code) {
    padding: 3px 6px;
    border-radius: 6px;
    background: #0f172a;
    color: #f8fafc;
    font-family: 'JetBrains Mono', Consolas, Monaco, monospace;
}

pre code.hljs {
    padding: 1.5rem 1rem 1.5rem;
    border-radius: 14px;
}

pre {
    position: relative;
}

pre:before {
    background: #fc625d;
    border-radius: 50%;
    box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;
    content: ' ';
    height: 10px;
    margin-top: 10px;
    margin-left: 10px;
    position: absolute;
    width: 10px;
}

.dark .article-content a {
    color: #7dd3fc;
}

.dark .article-content em {
    color: #a5b4fc;
}

.dark .article-content code:not(pre code) {
    background: rgba(51, 65, 85, 0.72);
    color: #e2e8f0;
}

.dark .article-content img {
    box-shadow: 0 20px 48px rgba(0, 0, 0, 0.36);
}
</style>
