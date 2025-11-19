<template>
  <div class="search-bar" ref="searchBarContainer">
    <input
        v-model="query"
        @input="filterSongs"
        @focus="handleFocus" type="text"
        class="form-control"
        placeholder="Wpisz tytuł..."
    />

    <ul v-if="filteredSongs.length && showResults" class="song-list mt-3">
      <li
          v-for="song in filteredSongs"
          :key="song.id"
          class="song-item"
          @click="selectSong(song)"
      >
        <div>
          <strong>{{ song.title }}</strong>
          <div class="artist">{{ song.artist }}</div>
        </div>
        <span class="badge">{{ song.genre || "Nieznany" }}</span>
      </li>
    </ul>

    <div v-else-if="showResults && showNoResultsMessage && query.length > 0 && filteredSongs.length === 0" class="song-item song-list mt-3">
      Brak wyników.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch} from 'vue'
import axios from 'axios'

const songs = ref([])
const filteredSongs = ref([])
const query = ref('')
let debounceTimer = null
const showNoResultsMessage = ref(false)
const showResults = ref(false)
let noResultsTimer = null
const emit = defineEmits(['song-selected'])

const searchBarContainer = ref(null)

function getToken() {
  return (
      localStorage.getItem('token') ||
      sessionStorage.getItem('token')
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

  } catch (err) {
    console.error("Błąd pobierania /api/songs:", err)
  }
}

function filterSongs() {
  const q = query.value.toLowerCase().trim()

  clearTimeout(debounceTimer)
  clearTimeout(noResultsTimer)
  showNoResultsMessage.value = false

  if (!q) {
    filteredSongs.value = []
    showResults.value = false
    return
  }

  showResults.value = true

  debounceTimer = setTimeout(() => {
    const results = songs.value.filter(song =>
        song.title.toLowerCase().includes(q)
    )
    filteredSongs.value = results

    if (results.length === 0) {
      noResultsTimer = setTimeout(() => {
        showNoResultsMessage.value = true
      }, 400)
    }
  }, 300)

}

function handleFocus() {
  if (query.value.length > 0) {
    showResults.value = true;
  }
}


function handleClickOutside(event) {
  if (searchBarContainer.value && !searchBarContainer.value.contains(event.target)) {
    showResults.value = false;
    showNoResultsMessage.value = false;
  }
}

function selectSong(song) {
  console.log("SearchBar: Emituję zdarzenie 'song-selected' z piosenką:", song);
  emit('song-selected', song);

  showResults.value = false;
  query.value = '';
  filteredSongs.value = [];
  showNoResultsMessage.value = false;
}

onMounted(() => {
  fetchSongs();
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

watch(query, (newValue) => {
  if (newValue.length === 0) {
    showResults.value = false;
    showNoResultsMessage.value = false;
  }
});

</script>

<style scoped>
.search-bar {
  width: 100%;
  max-width: 600px;
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 10;
}

.form-control {
  padding: 14px 20px;
  border: 2px solid #444;
  border-radius: 30px;
  background-color: #121212;
  color: #fff;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  width: 100%;
  box-sizing: border-box;
  z-index: 11;
}

.form-control:focus {
  border-color: #6C63FF;
  box-shadow: 0 0 15px rgba(108, 99, 255, 0.3);
  outline: none;
}

.form-control::placeholder {
  color: #888;
}

.song-list {
  list-style: none;
  padding: 0;
  margin: 0;

  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  width: 100%;
  box-sizing: border-box;

  background-color: #181818;
  border: 1px solid #333;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.8);

  max-height: 400px;
  overflow-y: auto;
  z-index: 100;
}

.song-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #2a2a2a;
  cursor: pointer;
  transition: background-color 0.2s;
}

.song-item:last-child {
  border-bottom: none;
}

.song-item:hover {
  background-color: #252525;
}

.song-info strong {
  display: block;
  color: #fff;
  font-size: 1.1em;
  margin-bottom: 4px;
}

.artist {
  color: #aaa;
  font-size: 0.9em;
}

.badge {
  background-color: #6C63FF;
  color: white;
  padding: 5px 10px;
  border-radius: 8px;
  font-size: 0.8em;
  white-space: nowrap;
}

.song-list::-webkit-scrollbar {
  width: 8px;
}
.song-list::-webkit-scrollbar-track {
  background: #181818;
  border-radius: 0 15px 15px 0;
}
.song-list::-webkit-scrollbar-thumb {
  background: #444;
  border-radius: 4px;
}
.song-list::-webkit-scrollbar-thumb:hover {
  background: #6C63FF;
}

.mt-3 {
  margin-top: 1rem;
}
</style>

