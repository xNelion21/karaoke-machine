<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 80px" @click="changeSort('id')">ID</th>
        <th style="width: 280px" @click="changeSort('songTitle')">Piosenka</th>
        <th style="width: 180px" @click="changeSort('username')">Użytkownik</th>
        <th style="width: 140px" @click="changeSort('status')">Status</th>
        <th style="width: 420px">Treść zgłoszenia</th>
        <th style="width: 200px">Akcje</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="s in pagedSuggestions" :key="s.id">
        <td>{{ s.id }}</td>
        <td>{{ s.songTitle || '—' }}</td>
        <td>{{ s.username }}</td>

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
              class="btn btn-sm btn-outline-success me-2"
              @click="acceptSuggestion(s.id)"
          >
            Akceptuj
          </button>

          <button
              v-if="s.status === 'PENDING'"
              class="btn btn-sm btn-outline-danger"
              @click="rejectSuggestion(s.id)"
          >
            Odrzuć
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination-footer">
      <button
          class="btn btn-sm btn-outline-light"
          :disabled="page <= 1"
          @click="prevPage"
      >
        Poprzednia
      </button>

      <span class="page-info">
        Strona {{ page }} z {{ totalPages }}
      </span>

      <button
          class="btn btn-sm btn-outline-light"
          :disabled="page >= totalPages"
          @click="nextPage"
      >
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

const emit = defineEmits(['page-change', 'update-sort'])

const auth = useAuthStore()
const fullSuggestions = ref([])
const pagedSuggestions = ref([])

async function loadSuggestions() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  // ⬅️ WAŻNE: backend obsługuje TYLKO pending
  const resp = await axios.get('/admin/suggestions/pending')
  let list = resp.data

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(s =>
        s.songTitle?.toLowerCase().includes(q) ||
        s.username?.toLowerCase().includes(q) ||
        s.id?.toString().includes(q)
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

  fullSuggestions.value = list
  applyPaging()
}

const totalPages = computed(() =>
    Math.max(1, Math.ceil(fullSuggestions.value.length / 10))
)

function applyPaging() {
  const amount = 10
  const start = (props.page - 1) * amount
  pagedSuggestions.value = fullSuggestions.value.slice(start, start + amount)
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

async function acceptSuggestion(id) {
  await axios.put(`/admin/suggestions/${id}/status`, null, {
    params: { action: 'ACCEPTED' }
  })
  await loadSuggestions()
}

async function rejectSuggestion(id) {
  await axios.put(`/admin/suggestions/${id}/status`, null, {
    params: { action: 'REJECTED' }
  })
  await loadSuggestions()
}

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadSuggestions)
onMounted(loadSuggestions)

function cropText(text) {
  if (!text) return ''
  return text.length > 180 ? text.slice(0, 180) + '…' : text
}

defineExpose({ loadSuggestions })
</script>

<style scoped>
.table-fixed {
  table-layout: fixed;
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
  color: white;
  align-items: center;
}

.page-info {
  font-weight: 500;
}
</style>

