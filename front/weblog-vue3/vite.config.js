import { fileURLToPath, URL } from 'node:url';
import path from 'path';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';

import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    proxy: {
      // Route all backend calls through the gateway (default listens on 80).
      '/api': { target: 'http://localhost:80', changeOrigin: true },
      '/admin': { target: 'http://localhost:80', changeOrigin: true },
      '/login': { target: 'http://localhost:80', changeOrigin: true },
    },
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/ai-robot/icons')],
      symbolId: 'icon-[name]',
      inject: 'body-last',
      customDomId: 'ai-robot-icons',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
});
