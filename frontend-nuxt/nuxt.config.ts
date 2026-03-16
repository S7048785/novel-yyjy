// https://nuxt.com/docs/api/configuration/nuxt-config

export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },
  modules: ["@nuxt/ui", "@pinia/nuxt"],
  ui: {
    fonts: false,
  },
  app: {
    pageTransition: { name: "page", mode: "out-in" },
    layoutTransition: { name: "layout", mode: "out-in" },
  },
  css: [
    // 1. Victor Mono (等宽字体，适合代码或数字)
    // "@fontsource-variable/victor-mono",

    // 2. Google Sans (无衬线字体，适合标题和正文)
    // 注意：google-sans 有时需要指定具体权重，index.css 通常包含 regular
    // "@fontsource/google-sans/400.css",

    // 3. Noto Serif SC (中文衬线字体，适合中文正文)
    // index.css 通常包含 Regular (400)
    // "@fontsource/noto-serif-sc/400.css",
    "./app/assets/css/main.css",

    // 【可选】如果你需要特定权重（如 Bold 700），可以额外添加：
    // '@fontsource/google-sans/700.css',
    // '@fontsource/noto-serif-sc/700.css',
  ],
  // 推荐：配置 Tailwind (如果你在用) 以识别这些字体
  // tailwindcss: {
  //   config: {
  //     theme: {
  //       extend: {
  //         fontFamily: {
  //           sans: ['"Google Sans"', "sans-serif"],
  //           serif: ['"Noto Serif SC"', "serif"],
  //           mono: ['"Victor Mono Variable"', '"JetBrains Mono"', "monospace"],
  //         },
  //       },
  //     },
  //   },
  // },
});
