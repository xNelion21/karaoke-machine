<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 100px" @click="changeSort('id')">ID</th>
        <th style="width: 350px" @click="changeSort('name')">Nazwa autora</th>
        <th style="width: 200px">Akcje</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="a in pagedAuthors" :key="a.id">
        <td>{{ a.id }}</td>
        <td>{{ a.name }}</td>
        <td>
          <button class="btn btn-sm btn-outline-warning me-2" @click="$emit('edit', a)">Edytuj</button>
          <button class="btn btn-sm btn-outline-danger" @click="$emit('delete', a.id)">Usuń</button>
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
import { ref, onMounted, watch, computed } from 'vue'
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
const fullAuthors = ref([])
const pagedAuthors = ref([])

async function loadAuthors() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  const resp = await axios.get('/authors')
  let list = resp.data

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(a => {
      return (
          a.name?.toLowerCase().includes(q) ||
          a.id?.toString().includes(q)
      )
    })
  }

  list.sort((a, b) => {
    if (props.sortBy === 'id') {
      return props.sortDir === 'asc'
          ? a.id - b.id
          : b.id - a.id
    }

    const name1 = a.name.toLowerCase()
    const name2 = b.name.toLowerCase()
    return props.sortDir === 'asc'
        ? name1.localeCompare(name2)
        : name2.localeCompare(name1)
  })

  fullAuthors.value = list
  applyPaging()
}

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(fullAuthors.value.length / 10))
})

function applyPaging() {
  const perPage = 10
  const start = (props.page - 1) * perPage
  const end = start + perPage
  pagedAuthors.value = fullAuthors.value.slice(start, end)
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

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadAuthors)
onMounted(loadAuthors)

defineExpose({ loadAuthors })
</script>

<style scoped>
.table-fixed {
  table-layout: fixed;
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
