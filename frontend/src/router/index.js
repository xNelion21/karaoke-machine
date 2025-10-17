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
      meta: {title : 'Strona główna'}
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
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },

      {
        path: '/app',
            name: 'app',
        component: AppView,
        meta: { title: 'Twoja aplikacja Karaoke' }
    }


];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

router.afterEach((to, from) => {
    document.title = `${to.meta.title} | Singly` || 'Singly';
});

export default router
