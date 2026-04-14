<template>
    <div class="min-h-screen flex flex-col">
        <Header></Header>

        <section class="relative overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-r from-emerald-500 via-slate-900 to-sky-500 opacity-90"></div>
            <div class="page-shell relative z-10 py-12">
                <p class="text-xs uppercase tracking-[0.35em] text-white/70 mb-2">Tags</p>
                <h1 class="text-3xl font-extrabold text-white mb-3">标签</h1>
                <p class="text-white/80 max-w-2xl">用标签拼一张个人兴趣地图。</p>
            </div>
        </section>

        <main class="page-shell -mt-8 pb-16 relative z-10 flex-1">
            <div class="grid grid-cols-12 gap-8">
                <div class="col-span-12 lg:col-span-8 space-y-4">
                    <TagListCard></TagListCard>
                </div>

                <aside class="col-span-12 lg:col-span-4 space-y-4">
                    <UserInfoCard></UserInfoCard>
                    <CategoryListCard></CategoryListCard>
                </aside>
            </div>
        </main>

        <ScrollToTopButton></ScrollToTopButton>

        <Footer class="mt-auto"></Footer>
    </div>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue';
import Footer from '@/layouts/frontend/components/Footer.vue';
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue';
import TagListCard from '@/layouts/frontend/components/TagListCard.vue';
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue';
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue';
</script>
