<template>
  <div class="search-bar d-flex flex-column" ref="searchBarContainer">
    <input
        v-model="localQuery"
        @input="handleInput"
        @focus="handleFocus"
        type="text"
        class="form-control"
        :placeholder="$t('app.search_placeholder')"
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
        <span v-if="song.categories && song.categories.length > 0" class="badge category-badge">
          {{ song.categories[0]?.name || song.categories[0] }}
        </span>

        <span v-else class="badge youtube-badge">
          <i class="bi bi-youtube"></i>
        </span>
      </li>
    </ul>

    <div v-else-if="showResults && songsStore.isLoading" class="song-item song-list mt-3 justify-content-center">
      <div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>
      <span class="text-light">{{ $t ('app.loading') }}</span>
    </div>

    <div v-else-if="showResults && localQuery && !songsStore.hasResults && !songsStore.isLoading" class="song-item song-list mt-3 text-light">
      {{ $t('app.no_results') }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useSongsStore } from '@/stores/songs'

const emit = defineEmits(['song-selected'])
const songsStore = useSongsStore()

const showResults = ref(false)
const searchBarContainer = ref(null)

const localQuery = ref(songsStore.searchQuery || '')
let debounceTimeout = null

function handleInput() {
  showResults.value = true

  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }

  debounceTimeout = setTimeout(() => {
    songsStore.setSearchQuery(localQuery.value)
  }, 600)
}

function handleFocus() {
  if (localQuery.value.length > 0) {
    showResults.value = true;
  }
}

function selectSong(song) {
  emit('song-selected', song);
  showResults.value = false;

  songsStore.clearSearch();
  localQuery.value = '';
}

function handleClickOutside(event) {
  if (searchBarContainer.value && !searchBarContainer.value.contains(event.target)) {
    showResults.value = false;
  }
}

watch(() => songsStore.searchQuery, (newVal) => {
  if (newVal !== localQuery.value) {
    localQuery.value = newVal;
  }
});

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
  margin-top: 20px;
}

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
  color: #aaa;
  opacity: 1;
}

.form-control:focus {
  border-color: #6C63FF;
  outline: none;
  box-shadow: 0 0 10px rgba(108, 99, 255, 0.5);
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
  z-index: 100;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.song-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.song-item:hover {
  background-color: #2a2a2a;
}

.badge {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  flex-shrink: 0;
  height: fit-content;
  line-height: 1;
}

.category-badge {
  background-color: rgba(108, 99, 255, 0.2);
  color: #9d96ff;
  border: 1px solid rgba(108, 99, 255, 0.3);
}

.youtube-badge {
  background-color: rgba(255, 0, 0, 0.15);
  color: #ff4d4d;
  border: 1px solid rgba(255, 0, 0, 0.3);
  font-size: 1.1rem;
  padding: 6px 10px;
}

.youtube-badge i {
  line-height: 0 !important;
  display: block;
  margin: 0;
  padding: 0;
}
</style>