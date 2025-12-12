<template>
  <div class="admin-page">
    <Navbar />

    <div class="admin-container container-fluid">
      <div class="row g-4">

        <aside class="col-lg-3 col-md-12 admin-sidebar">
          <div class="sidebar-inner">

            <h2 class="sidebar-title mb-4">
              <i class="bi bi-gear-fill me-2 text-accent"></i> Panel admina
            </h2>

            <nav class="sidebar-nav">

              <router-link class="sidebar-link" :to="{ name: 'admin-dashboard' }">
                <i class="bi bi-speedometer2 me-2"></i> Dashboard
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-users' }">
                <i class="bi bi-people-fill me-2"></i> Użytkownicy
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-songs' }">
                <i class="bi bi-music-note-list me-2"></i> Piosenki
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-authors' }">
                <i class="bi bi-person-badge-fill me-2"></i> Autorzy
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-categories' }">
                <i class="bi bi-tags-fill me-2"></i> Kategorie
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-suggestions' }">
                <i class="bi bi-lightbulb-fill me-2"></i> Sugestie
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-logs' }">
                <i class="bi bi-journal-text me-2"></i> Logi
              </router-link>

              <router-link class="sidebar-link" :to="{ name: 'admin-stats' }">
                <i class="bi bi-bar-chart-fill me-2"></i> Statystyki
              </router-link>

            </nav>

            <button class="btn-return mt-4" @click="goBackToApp">
              <i class="bi bi-arrow-left-circle me-2"></i>
              Powrót do aplikacji
            </button>

          </div>
        </aside>

        <main class="col-lg-9 col-md-12 admin-content">
          <div class="content-wrapper">
            <router-view />
          </div>
        </main>

      </div>
    </div>
  </div>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

function goBackToApp() {
  router.push('/app')
}
</script>


<style scoped>
.admin-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #181818 0%, #121020 100%);
  color: #fff;
  overflow-x: hidden;
}

.admin-container {
  margin-top: 20px;
}

.admin-sidebar {
  min-height: calc(100vh - 120px);
}

.sidebar-inner {
  background: rgba(30, 30, 35, 0.7);
  border-radius: 1rem;
  padding: 1.8rem;
  backdrop-filter: blur(6px);
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
  text-align: left;
}

.sidebar-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #9d96ff;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
}

.sidebar-link {
  padding: 12px 15px;
  margin-bottom: 10px;
  border-radius: 10px;
  color: #ddd;
  text-decoration: none;
  background: rgba(255,255,255,0.05);
  transition: 0.2s;
  font-size: 1.1rem;
}

.sidebar-link:hover {
  background: rgba(108, 99, 255, 0.2);
  transform: translateX(5px);
  color: #fff;
}

.sidebar-link.router-link-active {
  background: rgba(108, 99, 255, 0.35);
  color: #fff;
  font-weight: bold;
}

.btn-return {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  background: rgba(0,0,0,0.2);
  border: 1px solid rgba(255,255,255,0.15);
  color: #fff;
  font-weight: bold;
  transition: 0.2s;
}

.btn-return:hover {
  background: rgba(108,99,255,0.3);
  border-color: rgba(108,99,255,0.5);
}

.admin-content {
  min-height: calc(100vh - 120px);
}

.content-wrapper {
}
.text-accent {
  color: #9d96ff !important;
}
</style>