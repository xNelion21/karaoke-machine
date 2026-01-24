<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 60px" @click="changeSort('id')">ID</th>
        <th style="width: 160px" @click="changeSort('title')">Tytuł</th>
        <th style="width: 200px">Autorzy</th>
        <th style="width: 200px">Kategorie</th>
        <th style="width: 350px">Tekst</th>
        <th style="width: 160px">YouTube</th>
        <th style="width: 160px">Akcje</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="s in pagedSongs" :key="s.id">
        <td>{{ s.id }}</td>
        <td>{{ s.title }}</td>
        <td>{{ join(s.authors) }}</td>
        <td>{{ join(s.categories) }}</td>

        <td class="lyrics-cell">
          {{ cropLyrics(s.lyrics) }}
        </td>

        <td>
          <a v-if="s.youtubeUrl" :href="s.youtubeUrl" target="_blank">Link</a>
          <span v-else>—</span>
        </td>

        <td>
          <button
              class="btn btn-sm btn-outline-info me-1"
              @click="openPreview(s)"
              title="Podgląd tekstu"
          >
            <i class="bi bi-eye"></i>
          </button>

          <button
              class="btn btn-sm btn-outline-warning me-1"
              @click="$emit('edit', s)"
          >
            Edytuj
          </button>

          <button
              class="btn btn-sm btn-outline-danger"
              @click="$emit('delete', s.id)"
          >
            Usuń
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="previewItem" class="modal-overlay" @click.self="closePreview">
      <div class="modal-box">
        <div class="modal-header">
          <h4>{{ previewItem.title }}</h4>
          <button class="btn-close btn-close-white" @click="closePreview"></button>
        </div>

        <div class="modal-body">
          <div class="full-content-box">
            <pre>{{ previewItem.lyrics }}</pre>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closePreview">
            Zamknij
          </button>
        </div>
      </div>
    </div>

    <div class="pagination-footer">
      <button
          class="btn btn-sm btn-outline-light"
          :disabled="page <= 1"
          @click="$emit('page-change', page - 1)"
      >
        Poprzednia
      </button>

      <span class="page-info">
        Strona {{ page }} z {{ totalPages }}
      </span>

      <button
          class="btn btn-sm btn-outline-light"
          :disabled="page >= totalPages"
          @click="$emit('page-change', page + 1)"
      >
        Następna
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  search: String,
  sortBy: String,
  sortDir: String,
  page: Number
})

const emit = defineEmits(['page-change', 'edit', 'delete', 'update-sort'])

const auth = useAuthStore()

const fullSongs = ref([])
const pagedSongs = ref([])
const previewItem = ref(null)

async function loadSongs() {
  axios.defaults.headers.common.Authorization = `Bearer ${auth.token}`

  const base = (await axios.get('/songs')).data

  let list = await Promise.all(
      base.map(async s => {
        const d = (await axios.get(`/songs/${s.id}`)).data
        return { ...s, lyrics: d.lyrics, youtubeUrl: d.youtubeUrl }
      })
  )

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(s =>
        s.title?.toLowerCase().includes(q) ||
        s.authors?.join(',').toLowerCase().includes(q) ||
        s.categories?.join(',').toLowerCase().includes(q)
    )
  }

  list.sort((a, b) => {
    if (props.sortBy === 'id') {
      return props.sortDir === 'asc'
          ? a.id - b.id
          : b.id - a.id
    }

    const v1 = (a[props.sortBy] ?? '').toString().toLowerCase()
    const v2 = (b[props.sortBy] ?? '').toString().toLowerCase()

    return props.sortDir === 'asc'
        ? v1.localeCompare(v2)
        : v2.localeCompare(v1)
  })

  fullSongs.value = list
  applyPaging()
}

const totalPages = computed(() =>
    Math.max(1, Math.ceil(fullSongs.value.length / 8))
)

function applyPaging() {
  const start = (props.page - 1) * 8
  pagedSongs.value = fullSongs.value.slice(start, start + 8)
}

function changeSort(field) {
  emit('update-sort', field)
}

function cropLyrics(t) {
  if (!t) return '—'
  return t.replace(/\s+/g, ' ').slice(0, 120) + (t.length > 120 ? '…' : '')
}

function join(arr) {
  return arr?.length ? arr.join(', ') : '—'
}

function openPreview(song) {
  previewItem.value = song
}

function closePreview() {
  previewItem.value = null
}

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadSongs)
onMounted(loadSongs)

defineExpose({ loadSongs })
</script>

<style scoped>
.table-fixed {
  table-layout: fixed;
}

.lyrics-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-box {
  background: #212529;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  border-radius: 8px;
  border: 1px solid #444;
  color: white;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.modal-body {
  overflow-y: auto;
  flex: 1;
}

.full-content-box {
  background: #1a1d20;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #333;
}

.full-content-box pre {
  white-space: pre-wrap;
  margin: 0;
}

.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  color: white;
  align-items: center;
}

.page-info {
  font-weight: 500;
}
</style>
