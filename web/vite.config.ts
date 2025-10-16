import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    // 使用相对路径
    assetsDir: 'assets',
  },
  base: './', // 关键：使用相对路径
  server: {
    host: '0.0.0.0',
    port: 5173,
  },
});
