<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 120px" @click="changeSort('id')">ID</th>
        <th style="width: 400px" @click="changeSort('name')">Nazwa kategorii</th>
        <th style="width: 200px">Akcje</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="c in pagedCategories" :key="c.id">
        <td>{{ c.id }}</td>
        <td>{{ c.name }}</td>

        <td>
          <button class="btn btn-sm btn-outline-warning me-2" @click="$emit('edit', c)">Edytuj</button>
          <button class="btn btn-sm btn-outline-danger" @click="$emit('delete', c.id)">Usuń</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination-footer">
      <button class="btn btn-sm btn-outline-light" :disabled="page <= 1" @click="prevPage">
        Poprzednia
      </button>

      <span class="page-info">
        Strona {{ page }} z {{ totalPages }}
      </span>

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
const fullCategories = ref([])
const pagedCategories = ref([])

async function loadCategories() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  const resp = await axios.get('/categories')
  let list = resp.data

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(c => {
      return (
          c.name?.toLowerCase().includes(q) ||
          c.id?.toString().includes(q)
      )
    })
  }

  list.sort((a, b) => {
    if (props.sortBy === 'id') {
      return props.sortDir === 'asc'
          ? a.id - b.id
          : b.id - a.id
    }

    const v1 = (a.name ?? '').toLowerCase()
    const v2 = (b.name ?? '').toLowerCase()
    return props.sortDir === 'asc'
        ? v1.localeCompare(v2)
        : v2.localeCompare(v1)
  })

  fullCategories.value = list
  applyPaging()
}

const totalPages = computed(() =>
    Math.max(1, Math.ceil(fullCategories.value.length / 10))
)

function applyPaging() {
  const amount = 10
  const start = (props.page - 1) * amount
  pagedCategories.value = fullCategories.value.slice(start, start + amount)
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

watch(() => [props.search, props.sortBy, props.sortDir, props.page], loadCategories)
onMounted(loadCategories)

defineExpose({ loadCategories })
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
