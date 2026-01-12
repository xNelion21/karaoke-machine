<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 60px" @click="changeSort('id')">ID</th>
        <th style="width: 160px" @click="changeSort('title')">Tytuł</th>
        <th style="width: 160px" @click="changeSort('artist')">Artysta</th>
        <th style="width: 130px" @click="changeSort('genre')">Gatunek</th>
        <th style="width: 350px">Tekst</th>
        <th style="width: 200px">Youtube</th>
        <th style="width: 120px">Akcje</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="s in pagedSongs" :key="s.id">
        <td>{{ s.id }}</td>
        <td>{{ s.title }}</td>
        <td>{{ formatArray(s.authors) }}</td>
        <td>{{ s.genre }}</td>

        <td class="lyrics-cell">
          <pre>{{ cropLyrics(s.lyrics) }}</pre>
        </td>

        <td>
          <a v-if="s.youtubeUrl" :href="s.youtubeUrl" target="_blank">Link</a>
          <span v-else>---</span>
        </td>

        <td>
          <button class="btn btn-sm btn-outline-warning me-1" @click="$emit('edit', s)">Edytuj</button>
          <button class="btn btn-sm btn-outline-danger" @click="$emit('delete', s.id)">Usuń</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination-footer">
      <button class="btn btn-sm btn-outline-light" :disabled="page <= 1" @click="prevPage">
        Poprzednia
      </button>

      <span class="page-info">Strona {{ page }} z {{ totalPages }}</span>

      <button class="btn btn-sm btn-outline-light" :disabled="page >= totalPages" @click="nextPage">
        Następna
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  search: String,
  sortBy: String,
  sortDir: String,
  page: Number
})

const emit = defineEmits(['page-change', 'delete', 'edit'])
const auth = useAuthStore()

const fullSongs = ref([])
const pagedSongs = ref([])
const hasMore = ref(false)

async function loadSongs() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  const resp = await axios.get('/songs')
  let list = resp.data

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(s => {
      return (
          s.title?.toLowerCase().includes(q) ||
          s.genre?.toLowerCase().includes(q) ||
          s.authors?.join(',').toLowerCase().includes(q) ||
          s.id?.toString().includes(q)
      )
    })
  }

  list.sort((a, b) => {
    if (props.sortBy === 'id') {
      return props.sortDir === 'asc'
          ? a.id - b.id
          : b.id - a.id
    }

    let v1 = (a[props.sortBy] ?? '').toString().toLowerCase()
    let v2 = (b[props.sortBy] ?? '').toString().toLowerCase()

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
  hasMore.value = start + 8 < fullSongs.value.length
}

function nextPage() {
  emit('page-change', props.page + 1)
}

function prevPage() {
  emit('page-change', props.page - 1)
}

function changeSort(field) {
  emit('update-sort', field)
}

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadSongs)
onMounted(loadSongs)

function cropLyrics(txt) {
  if (!txt) return '---'
  return txt.length > 120 ? txt.slice(0, 120) + '...' : txt
}

function formatArray(arr) {
  if (!arr) return '-'
  return arr.join(', ')
}
defineExpose({ loadSongs })
</script>

<style scoped>
.table-fixed {
  table-layout: fixed;
}
.lyrics-cell {
  max-height: 80px;
  overflow: hidden;
  white-space: pre-wrap;
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
