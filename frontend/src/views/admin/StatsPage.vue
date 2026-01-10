<template>
  <div class="dashboard-page">
    <h1 class="admin-title">Dashboard</h1>

    <h2 class="section-title">System</h2>
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
    </div>

    <h2 class="section-title">Użytkownicy</h2>
    <div class="stats-grid">
      <div class="stat-card">
        <h2>{{ blockedUsersCount }}</h2>
        <p>Zablokowani</p>
      </div>

      <div class="stat-card">
        <h2>{{ blockedUsersPercent }}%</h2>
        <p>Procent zablokowanych</p>
      </div>

      <div class="stat-card">
        <h2>{{ myLikedSongsCount }}</h2>
        <p>Moje polubione piosenki</p>
      </div>
    </div>

    <h2 class="section-title">Muzyka</h2>
    <div class="stats-grid">
      <div class="stat-card">
        <h2>{{ avgSongsPerAuthor }}</h2>
        <p>Śr. piosenek na autora</p>
      </div>

      <div class="stat-card">
        <h2>{{ avgSongsPerCategory }}</h2>
        <p>Śr. piosenek na kategorię</p>
      </div>
    </div>

    <h2 class="section-title">Sugestie</h2>
    <div class="stats-grid">
      <div class="stat-card pending">
        <h2>{{ pendingSuggestions }}</h2>
        <p>Oczekujące</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios"
import { ref, onMounted, watch } from "vue"
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()

const usersCount = ref(0)
const blockedUsersCount = ref(0)
const blockedUsersPercent = ref(0)

const songsCount = ref(0)
const authorsCount = ref(0)
const categoriesCount = ref(0)

const avgSongsPerAuthor = ref(0)
const avgSongsPerCategory = ref(0)

const pendingSuggestions = ref(0)
const myLikedSongsCount = ref(0)

async function loadStats() {
  if (!auth.token) return

  axios.defaults.headers.common["Authorization"] = `Bearer ${auth.token}`

  const users = (await axios.get("/admin/users")).data || []
  usersCount.value = users.length
  blockedUsersCount.value = users.filter(u => u.locked).length
  blockedUsersPercent.value = usersCount.value
      ? Math.round((blockedUsersCount.value / usersCount.value) * 100)
      : 0

  const songs = (await axios.get("/songs")).data || []
  songsCount.value = songs.length

  const authors = (await axios.get("/authors")).data || []
  authorsCount.value = authors.length
  avgSongsPerAuthor.value = authorsCount.value
      ? Math.round(songsCount.value / authorsCount.value)
      : 0

  const categories = (await axios.get("/categories")).data || []
  categoriesCount.value = categories.length
  avgSongsPerCategory.value = categoriesCount.value
      ? Math.round(songsCount.value / categoriesCount.value)
      : 0

  const suggestions = (await axios.get("/admin/suggestions/pending")).data || []
  pendingSuggestions.value = suggestions.length

  const me = (await axios.get("/users/me")).data
  myLikedSongsCount.value = me?.likedSongIds?.length || 0
}

onMounted(loadStats)
watch(() => auth.token, loadStats)
</script>

<style scoped>
:global(html, body) {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

.dashboard-page {
  height: 100%;
  padding: 18px;
  color: white;
  font-family: 'Inter', sans-serif;
  overflow: hidden;
}

.admin-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 16px;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 22px 0 10px;
  opacity: 0.9;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 14px;
}

.stat-card {
  background: rgba(20,20,20,0.85);
  padding: 14px 12px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 3px 8px rgba(0,0,0,0.25);
}

.stat-card h2 {
  font-size: 2.35rem;
  margin: 0;
  font-weight: 700;
  color: #9d96ff;
}

.stat-card p {
  margin-top: 4px;
  font-size: 1.1rem;
  opacity: 0.9;
}

.pending h2 {
  color: #ffcc66;
}
</style>






