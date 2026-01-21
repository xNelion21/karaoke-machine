<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 80px" @click="changeSort('id')">ID</th>
        <th style="width: 250px" @click="changeSort('songTitle')">Piosenka</th>
        <th style="width: 150px" @click="changeSort('username')">Użytkownik</th>
        <th style="width: 120px" @click="changeSort('status')">Status</th>
        <th style="width: 350px">Treść zgłoszenia</th> <th style="width: 200px">Akcje</th>
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
          <div class="d-flex justify-content-between align-items-start">
            <pre class="text-preview mb-0">{{ cropText(s.proposedContent) }}</pre>

            <button
                class="btn btn-sm btn-outline-info ms-2"
                @click="openPreview(s)"
                title="Zobacz całość"
            >
              <i class="bi bi-eye"></i> </button>
          </div>
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

    <div v-if="previewItem" class="modal-overlay" @click.self="closePreview">
      <div class="modal-box">
        <div class="modal-header">
          <h4>Podgląd zgłoszenia #{{ previewItem.id }}</h4>
          <button class="btn-close btn-close-white" @click="closePreview"></button>
        </div>

        <div class="modal-body">
          <p class="text-light mb-1">Piosenka: <strong class="text-white">{{ previewItem.songTitle }}</strong></p>
          <p class="text-light mb-3">Użytkownik: <strong class="text-white">{{ previewItem.username }}</strong></p>
          <hr class="border-secondary">

          <label class="text-light small mb-2">Pełna treść:</label>
          <div class="full-content-box">
            <pre>{{ previewItem.proposedContent }}</pre>
          </div>
        </div>

        <div class="modal-footer mt-3 d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="closePreview">Zamknij</button>

          <template v-if="previewItem.status === 'PENDING'">
            <button class="btn btn-success" @click="acceptFromModal(previewItem.id)">Akceptuj</button>
            <button class="btn btn-danger" @click="rejectFromModal(previewItem.id)">Odrzuć</button>
          </template>
        </div>
      </div>
    </div>

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

// ZMIANA: Zmienna przechowująca aktualnie podglądany element
const previewItem = ref(null)

async function loadSuggestions() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`
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
      return props.sortDir === 'asc' ? a.id - b.id : b.id - a.id
    }
    const v1 = (a[props.sortBy] ?? '').toString().toLowerCase()
    const v2 = (b[props.sortBy] ?? '').toString().toLowerCase()
    return props.sortDir === 'asc' ? v1.localeCompare(v2) : v2.localeCompare(v1)
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

function nextPage() { emit('page-change', props.page + 1) }
function prevPage() { emit('page-change', props.page - 1) }
function changeSort(field) { emit('update-sort', field) }

// ZMIANA: Obsługa modala
function openPreview(item) {
  previewItem.value = item
}

function closePreview() {
  previewItem.value = null
}

async function acceptSuggestion(id) {
  await axios.put(`/admin/suggestions/${id}/status`, null, { params: { action: 'ACCEPTED' } })
  await loadSuggestions()
}

async function rejectSuggestion(id) {
  await axios.put(`/admin/suggestions/${id}/status`, null, { params: { action: 'REJECTED' } })
  await loadSuggestions()
}

// ZMIANA: Obsługa akcji z wnętrza modala
async function acceptFromModal(id) {
  await acceptSuggestion(id)
  closePreview()
}

async function rejectFromModal(id) {
  await rejectSuggestion(id)
  closePreview()
}

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadSuggestions)
onMounted(loadSuggestions)

function cropText(text) {
  if (!text) return ''
  return text.length > 100 ? text.slice(0, 100) + '…' : text // Zmniejszyłem limit dla tabeli
}

defineExpose({ loadSuggestions })
</script>

<style scoped>
.table-fixed {
  table-layout: fixed;
}

.text-preview {
  max-height: 80px;
  overflow: hidden;
  white-space: pre-wrap;
  font-size: 12px;
  color: #ccc;
  margin-bottom: 0;
}

/* Style dla Modala */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7); /* Przyciemnienie tła */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(3px);
}

.modal-box {
  background: #212529; /* Ciemne tło bootstrapowe */
  width: 90%;
  max-width: 800px; /* Szeroki modal dla tekstu */
  max-height: 90vh;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  border: 1px solid #444;
  color: white;
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
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
  white-space: pre-wrap; /* Zawijanie wierszy */
  font-family: monospace;
  font-size: 14px;
  color: #e0e0e0;
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