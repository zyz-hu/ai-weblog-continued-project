<template>
   <!-- 设置语言为中文-->
   <el-config-provider :locale="locale">
      <div class="app-shell">
         <router-view class="app-view"></router-view>
      </div>
   </el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
const locale = zhCn
</script>

<style>
/* 自定义顶部加载 Loading 颜色 */
#nprogress .bar {
   background: #409eff!important;
}

#app {
   min-height: 100vh;
}

.app-shell {
   min-height: 100vh;
   display: flex;
   flex-direction: column;
}

.app-view {
   flex: 1;
   display: flex;
   flex-direction: column;
}
</style>
