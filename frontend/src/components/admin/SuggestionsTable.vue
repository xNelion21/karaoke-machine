<template>
  <div>

    <div class="table-wrapper">
      <table class="table table-dark table-striped table-fixed">
        <thead>
        <tr>
          <th style="width: 60px">ID</th>
          <th style="width: 200px">Piosenka</th>
          <th style="width: 200px">Użytkownik</th>
          <th style="width: 150px">Status</th>
          <th style="width: 350px">Treść zgłoszenia</th>
          <th style="width: 180px">Akcje</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="s in pagedSuggestions" :key="s.id">
          <td>{{ s.id }}</td>
          <td>{{ s.song }}</td>
          <td>{{ s.user }}</td>
          <td>
            <span v-if="s.status === 'PENDING'" class="text-warning">Oczekuje</span>
            <span v-else-if="s.status === 'ACCEPTED'" class="text-success">Zaakceptowana</span>
            <span v-else class="text-danger">Odrzucona</span>
          </td>

          <td class="content-cell">
            <pre>{{ cropText(s.proposedContent) }}</pre>
          </td>

          <td>
            <button
                v-if="s.status === 'PENDING'"
                class="btn btn-sm btn-success me-1"
                @click="acceptSuggestion(s)"
            >
              Akceptuj
            </button>

            <button
                v-if="s.status === 'PENDING'"
                class="btn btn-sm btn-danger me-1"
                @click="rejectSuggestion(s)"
            >
              Odrzuć
            </button>

            <button
                class="btn btn-sm btn-outline-danger"
                @click="deleteSuggestion(s.id)"
            >
              Usuń
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-footer">
      <button class="btn btn-sm btn-outline-light" :disabled="page <= 1" @click="prevPage">
        Poprzednia
      </button>

      <span class="page-info">Strona {{ page }} z {{ totalPages }}</span>

      <button class="btn btn-sm btn-outline-light" :disabled="!hasMore" @click="nextPage">
        Następna
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  search: String,
  sortBy: String,
  sortDir: String,
  page: Number
})

const emit = defineEmits(['page-change'])

const auth = useAuthStore()

const fullSuggestions = ref([])
const pagedSuggestions = ref([])

const hasMore = ref(false)

async function loadSuggestions() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  const resp = await axios.get('/api/admin/suggestions/pending')
  let list = resp.data

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(s =>
        s.song.title.toLowerCase().includes(q) ||
        s.user.username.toLowerCase().includes(q) ||
        s.id.toString().includes(q)
    )
  }

  list = list.map(s => ({
    id: s.id,
    song: s.song.title,
    user: s.user.username,
    proposedContent: s.proposedContent,
    status: s.status
  }))

  list.sort((a, b) => {
    if (props.sortBy === 'id') {
      return props.sortDir === 'asc'
          ? a.id - b.id
          : b.id - a.id
    }

    const f1 = a[props.sortBy].toString().toLowerCase()
    const f2 = b[props.sortBy].toString().toLowerCase()

    return props.sortDir === 'asc'
        ? f1.localeCompare(f2)
        : f2.localeCompare(f1)
  })

  fullSuggestions.value = list
  applyPaging()
}

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(fullSuggestions.value.length / 8))
})

function applyPaging() {
  const perPage = 8
  const start = (props.page - 1) * perPage
  const end = start + perPage

  pagedSuggestions.value = fullSuggestions.value.slice(start, end)
  hasMore.value = end < fullSuggestions.value.length
}

function nextPage() {
  emit('page-change', props.page + 1)
}

function prevPage() {
  emit('page-change', props.page - 1)
}

async function acceptSuggestion(s) {
  await axios.put(`/api/admin/suggestions/${s.id}/status?action=accepted`)
  await loadSuggestions()
}

async function rejectSuggestion(s) {
  await axios.put(`/api/admin/suggestions/${s.id}/status?action=rejected`)
  await loadSuggestions()
}

async function deleteSuggestion(id) {
  await axios.delete(`/api/admin/suggestions/${id}`)
  await loadSuggestions()
}

watch(
    () => [props.search, props.sortBy, props.sortDir, props.page],
    loadSuggestions
)

onMounted(loadSuggestions)

function cropText(s) {
  if (!s) return ''
  return s.length > 120 ? s.slice(0, 120) + '...' : s
}
</script>

<style scoped>
.table-wrapper {
  max-height: calc(100vh - 260px);
  overflow-y: auto;
}

.table-fixed {
  table-layout: fixed;
}

.content-cell {
  max-width: 350px;
}

.content-cell pre {
  max-height: 100px;
  overflow-y: auto;
  white-space: pre-wrap;
  font-size: 12px;
}

.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.page-info {
  color: white;
  font-size: 14px;
}
</style>