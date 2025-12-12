<template>
  <div class="dashboard-page">
    <h1 class="admin-title">Dashboard</h1>

    <div class="stats-grid">
      <div class="stat-card">
        <h2>{{ usersCount }}</h2>
        <p>Użytkownicy</p>
      </div>

      <div class="stat-card">
        <h2>{{ songsCount }}</h2>
        <p>Piosenki</p>
      </div>

      <div class="stat-card">
        <h2>{{ authorsCount }}</h2>
        <p>Autorzy</p>
      </div>

      <div class="stat-card">
        <h2>{{ categoriesCount }}</h2>
        <p>Kategorie</p>
      </div>

      <div class="stat-card pending">
        <h2>{{ pendingSuggestions }}</h2>
        <p>Sugestie oczekujące</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios"
import { ref, onMounted } from "vue"
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()

const usersCount = ref(0)
const songsCount = ref(0)
const authorsCount = ref(0)
const categoriesCount = ref(0)
const pendingSuggestions = ref(0)

async function loadStats() {
  axios.defaults.headers.common["Authorization"] = `Bearer ${auth.token}`

  const [users, songs, authors, categories, suggestions] = await Promise.all([
    axios.get("/admin/users"),
    axios.get("/songs"),
    axios.get("/authors"),
    axios.get("/categories"),
    axios.get("/admin/suggestions/pending")
  ])

  usersCount.value = users.data.length
  songsCount.value = songs.data.length
  authorsCount.value = authors.data.length
  categoriesCount.value = categories.data.length
  pendingSuggestions.value = suggestions.data.length
}

onMounted(loadStats)
</script>

<style scoped>
.dashboard-page {
  padding: 24px;
  color: white;
  font-family: 'Inter', sans-serif;
}

.admin-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.stat-card {
  background: rgba(20,20,20,0.85);
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  backdrop-filter: blur(6px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.25);
}

.stat-card h2 {
  font-size: 2.4rem;
  margin: 0;
  font-weight: 700;
  color: #9d96ff;
}

.stat-card p {
  margin-top: 8px;
  opacity: 0.85;
  font-size: 1.1rem;
}

.pending h2 {
  color: #ffcc66;
}
</style>
