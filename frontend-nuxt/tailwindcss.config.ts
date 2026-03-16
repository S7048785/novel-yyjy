import type { Config } from "tailwindcss";
import defaultTheme from "tailwindcss/defaultTheme";

export default <Partial<Config>>{
  content: [
    "./pages/**/*.{js,ts,vue}",
    "./components/**/*.{js,ts,vue}",
    "./layouts/**/*.{js,ts,vue}",
  ],
  theme: {
    extend: {
      colors: {},
    },
  },
};
