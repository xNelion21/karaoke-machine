<template>
  <div class="search-bar">
    <input
        v-model="query"
        @input="filterSongs"
        type="text"
        class="form-control"
        placeholder="Wpisz tytuł..."
    />

    <ul v-if="filteredSongs.length" class="song-list mt-3">
      <li
          v-for="song in filteredSongs"
          :key="song.id"
          class="song-item"
      >
        <div>
          <strong>{{ song.title }}</strong>
          <div class="artist">{{ song.artist }}</div>
        </div>
        <span class="badge">{{ song.genre || "Nieznany" }}</span>
      </li>
    </ul>

    <div v-else-if="query.length > 0" class="mt-3">
      Brak wyników.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const songs = ref([])
const filteredSongs = ref([])
const query = ref('')
let debounceTimer = null

function getToken() {
  return (
      localStorage.getItem('token') ||
      sessionStorage.getItem('token') ||
      null
  )
}

async function fetchSongs() {
  const token = getToken()

  if (!token) {
    console.warn("Brak tokena — nie pobieram piosenek")
    return
  }

  try {
    const res = await axios.get('http://localhost:8080/api/songs', {
      headers: { Authorization: `Bearer ${token}` }
    })

    console.log("songs response:", res.data)

    songs.value = res.data
    filteredSongs.value = res.data

  } catch (err) {
    console.error("Błąd pobierania /api/songs:", err)
  }
}

function filterSongs() {
  clearTimeout(debounceTimer)

  debounceTimer = setTimeout(() => {
    const q = query.value.toLowerCase()

    filteredSongs.value = songs.value.filter(song =>
        song.title.toLowerCase().includes(q)
    )
  }, 200)
}

onMounted(fetchSongs)
</script>

<style scoped>
.search-bar {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-control {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #555;
  background: transparent;
  color: #fff;
}

.form-control::placeholder {
  color: #ccc;
}

.song-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.song-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #444;
}

.artist {
  font-size: 0.9em;
  color: #aaa;
}

.badge {
  background: #6C63FF;
  padding: 3px 8px;
  border-radius: 8px;
  color: #fff;
  font-size: 0.8rem;
}
</style>


