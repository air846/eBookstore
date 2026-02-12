import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes("node_modules")) return;
          if (id.includes("element-plus")) return "vendor-element-plus";
          if (id.includes("echarts")) return "vendor-echarts";
          if (id.includes("vue-router")) return "vendor-vue-router";
          if (id.includes("pinia")) return "vendor-pinia";
          if (id.includes("axios")) return "vendor-axios";
          if (id.includes("vue")) return "vendor-vue";
          return "vendor";
        }
      }
    }
  },
  server: {
    port: 5174
  }
});
