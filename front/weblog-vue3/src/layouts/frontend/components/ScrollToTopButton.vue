<template>
    <!-- fixed 固定位置，并通过 bottom-xx right-xx 设置在右下角 -->
    <div v-show="showScrollToTopBtn" @click="scrollToTop"
        class="fixed bottom-2 right-2 z-40 flex h-12 w-12 cursor-pointer items-center justify-center rounded-2xl border border-white/10 bg-slate-950/88 text-slate-50 shadow-[0_18px_38px_rgba(2,6,23,0.34)] backdrop-blur-md transition-all duration-200 hover:-translate-y-0.5 hover:bg-slate-900/96 hover:shadow-[0_22px_44px_rgba(2,6,23,0.46)] md:bottom-10 md:right-10"
        aria-label="回到顶部">
        <svg class="h-[18px] w-[18px] text-slate-50" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 10 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2.3"
                d="M5 13V1m0 0L1 5m4-4 4 4"></path>
        </svg>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// 是否展示返回顶部按钮
const showScrollToTopBtn = ref(false)

// 添加滚动监听
onMounted(() => window.addEventListener('scroll', handleScroll))

// 移除滚动监听
onBeforeUnmount(() => window.removeEventListener('scroll', handleScroll))

const handleScroll = () => {
    // 如果页面滚动超过300px，显示回到顶部按钮，否则隐藏
    showScrollToTopBtn.value = window.scrollY > 300
}

// 滚动到顶部
const scrollToTop = () => {
    window.scrollTo({
        top: 0, // 距离顶部位置
        behavior: 'smooth' // 平滑滚动效果
    });
}
</script>
