import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import {useAuthStore} from "@/stores/auth.js";

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
        path: '/app',
        name: 'app',
        component: () => import('../views/AppView.vue'),
        meta: { title: 'Twoja aplikacja Karaoke', requiresAuth: true }
    },
    {
        path: '/admin',
        component: () => import('@/views/admin/AdminLayout.vue'),
        meta: { title: 'Panel administratora', requiresAuth: true, requiresAdmin: true},
        children: [
            {
                path: '',
                name: 'admin-dashboard',
                component: () => import('@/views/admin/StatsPage.vue'),
            },
            {
                path: 'users',
                name: 'admin-users',
                component: () => import('@/views/admin/UsersPage.vue')
            },
            {
                path: 'songs',
                name: 'admin-songs',
                component: () => import('@/views/admin/SongsPage.vue')
            },
            {
                path: 'authors',
                name: 'admin-authors',
                component: () => import('@/views/admin/AuthorPage.vue')
            },
            {
                path: 'categories',
                name: 'admin-categories',
                component: () => import('@/views/admin/CategoryPage.vue')
            },
            {
                path: 'suggestions',
                name: 'admin-suggestions',
                component: () => import('@/views/admin/SuggestionsPage.vue')
            },
            {
                path: 'stats',
                name: 'admin-stats',
                component: () => import('@/views/admin/StatsPage.vue')
            }
        ]

    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})


router.beforeEach(async (to, from, next) => {
    const auth = useAuthStore()

    if (to.meta.requiresAuth && !auth.token) {
        return next('/login')
    }

    if (to.meta.requiresAdmin) {
        if (!auth.user) {
            await auth.fetchUser()
        }

        if (auth.user?.role !== 'ROLE_ADMIN') {
            return next('/')
        }
    }

    next()
})

router.afterEach((to) => {
    document.title = `${to.meta.title} | Singly` || 'Singly'
})

export default router