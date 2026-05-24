/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx}'
  ],
  theme: {
    extend: {
      colors: {
        ink: {
          950: '#0b0b0f',
          900: '#171823',
          800: '#202135',
          700: '#2d2f46'
        },
        sand: {
          50: '#f7f2ea',
          100: '#efe4d6',
          200: '#e4ceb6',
          300: '#d6b28f',
          400: '#c5905b',
          500: '#b4743c'
        },
        tide: {
          50: '#e6f5f3',
          100: '#c9ece6',
          200: '#98ded4',
          300: '#5cc6b8',
          400: '#2ea5a2',
          500: '#1c7d7f'
        },
        coral: {
          50: '#fff1ec',
          100: '#ffd6c9',
          200: '#ffb098',
          300: '#ff8a66',
          400: '#ff6d3f',
          500: '#e6522a'
        }
      },
      fontFamily: {
        sans: ['Space Grotesk', 'Segoe UI', 'sans-serif'],
        serif: ['Fraunces', 'Georgia', 'serif']
      },
      boxShadow: {
        glow: '0 24px 60px -30px rgba(12, 120, 122, 0.65)',
        card: '0 24px 60px -40px rgba(17, 17, 24, 0.6)'
      },
      backgroundImage: {
        'hero-texture': 'radial-gradient(circle at 20% 20%, rgba(46, 165, 162, 0.22), transparent 55%), radial-gradient(circle at 80% 0%, rgba(255, 109, 63, 0.25), transparent 40%), linear-gradient(120deg, rgba(11, 11, 15, 0.98), rgba(23, 24, 35, 0.92))'
      }
    }
  },
  plugins: []
}
