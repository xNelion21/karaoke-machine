<template>
  <div class="search-bar d-flex flex-column" ref="searchBarContainer">
    <input
        :value="songsStore.searchQuery"
        @input="handleInput"
        @focus="handleFocus"
        type="text"
        class="form-control"
        placeholder="Wpisz tytuł..."
    />

    <ul v-if="showResults && songsStore.hasResults" class="song-list mt-3 shadow-lg">
      <li
          v-for="song in songsStore.filteredSongs"
          :key="song.id"
          class="song-item"
          @click="selectSong(song)"
      >
        <div class="song-info">
          <strong>{{ song.title }}</strong>
          <div class="artist">{{ song.artist }}</div>
        </div>
        <span class="badge">{{ song.categories?.[0] || "Inne" }}</span>
      </li>
    </ul>

    <div v-else-if="showResults && songsStore.searchQuery && !songsStore.hasResults" class="song-item song-list mt-3">
      Brak wyników.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useSongsStore } from '@/stores/songs'

const emit = defineEmits(['song-selected'])
const songsStore = useSongsStore()

const showResults = ref(false)
const searchBarContainer = ref(null)

function handleInput(event) {
  songsStore.setSearchQuery(event.target.value)
  showResults.value = true
}

function handleFocus() {
  if (songsStore.searchQuery.length > 0) {
    showResults.value = true;
  }
}

function selectSong(song) {
  emit('song-selected', song);

  showResults.value = false;
  songsStore.clearSearch();
}

function handleClickOutside(event) {
  if (searchBarContainer.value && !searchBarContainer.value.contains(event.target)) {
    showResults.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>

.search-bar {
  width: 100%;
  max-width: 600px;
  position: relative;
  z-index: 10;
  margin-top: 20px; }

.form-control {
  padding: 14px 20px;
  border: 1px solid #333;
  border-radius: 30px;
  background-color: #1a1a1a;
  color: #fff;
  font-size: 1.1rem;
  width: 100%;
}

.form-control::placeholder {
  color: #9e9e9e;
}

.form-control:focus {
  border-color: #6C63FF;
  outline: none;
}

.song-list {
  list-style: none;
  padding: 0;
  margin: 0;
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  width: 100%;
  background-color: #1e1e1e;
  border: 1px solid #444;
  border-radius: 10px;
  max-height: 400px;
  overflow-y: auto;
  z-index: 100;
}

.song-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #333;
  cursor: pointer;
}

.song-item:hover {
  background-color: #2a2a2a;
}

.badge {
  background-color: #6C63FF;
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.75em;
}
</style>