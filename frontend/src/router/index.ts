import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/outline',
    name: 'Outline',
    component: () => import('@/views/outline/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/characters',
    name: 'Characters',
    component: () => import('@/views/character/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/locations',
    name: 'Locations',
    component: () => import('@/views/location/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/world-map',
    name: 'WorldMap',
    component: () => import('@/views/world-map/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/timelines',
    name: 'Timelines',
    component: () => import('@/views/timeline/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/events',
    name: 'Events',
    component: () => import('@/views/event/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('@/views/item/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/audio',
    name: 'Audio',
    component: () => import('@/views/audio/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/images',
    name: 'Images',
    component: () => import('@/views/image/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/videos',
    name: 'Videos',
    component: () => import('@/views/video/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router