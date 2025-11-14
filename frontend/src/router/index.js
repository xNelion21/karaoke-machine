import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import AppView from '@/views/AppView.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView,
        meta: { title: 'Strona główna' }
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: { title: 'Zaloguj się' }
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterView,
        meta: { title: 'Zarejestruj się' }
    },
    {
        path: '/about',
        name: 'about',
        component: () => import('../views/AboutView.vue')
    },
    {
        path: '/app',
        name: 'app',
        component: AppView,
        meta: { title: 'Twoja aplikacja Karaoke', requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    const loggedIn = localStorage.getItem('token') || sessionStorage.getItem('token')

    if (to.path === '/login' && loggedIn) {
        next('/app')
    }

    if (to.meta.requiresAuth && !loggedIn) {
        next('/login')
    } else {
        next()
    }
})

router.afterEach((to) => {
    document.title = `${to.meta.title} | Singly` || 'Singly'
})

export default router

