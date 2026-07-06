/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        navy: {
          950: '#0A1F33',
          900: '#0F2B46',
          800: '#163C5E',
          700: '#1F4E76',
        },
        teal: {
          600: '#0E7C7B',
          500: '#129593',
        },
        sand: {
          100: '#F6F4EF',
          200: '#EFEBE2',
          300: '#E2DDD1',
        },
        rust: '#B3432B',
        amber: '#C97A2B',
        moss: '#1F8A55',
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'sans-serif'],
        body: ['"Inter"', 'sans-serif'],
        mono: ['"IBM Plex Mono"', 'monospace'],
      },
      borderRadius: {
        sm: '4px',
        DEFAULT: '6px',
      },
    },
  },
  plugins: [],
}
