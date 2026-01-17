<template>
  <div>
    <table class="table table-dark table-striped table-fixed">
      <thead>
      <tr>
        <th style="width: 60px" @click="changeSort('id')">ID</th>
        <th style="width: 180px" @click="changeSort('username')">Username</th>
        <th style="width: 220px" @click="changeSort('email')">Email</th>
        <th style="width: 160px" @click="changeSort('role')">Rola</th>
        <th style="width: 120px" @click="changeSort('locked')">Zablokowany</th>
        <th style="width: 160px">Akcje</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="u in pagedUsers" :key="u.id">
        <td>{{ u.id }}</td>
        <td>{{ u.username }}</td>
        <td>{{ u.email }}</td>

        <td>
          <div class="role-select-wrapper">
            <select
                class="role-select"
                :value="u.role"
                @change="onRoleChange(u, $event)"
            >
              <option value="ROLE_USER">USER</option>
              <option value="ROLE_ADMIN">ADMIN</option>
            </select>
          </div>
        </td>

        <td>{{ u.locked ? 'Tak' : 'Nie' }}</td>

        <td>
          <button
              class="btn btn-sm lock-btn me-1"
              :class="u.locked ? 'unlock' : 'lock'"
              @click="toggleLock(u)"
          >
            {{ u.locked ? 'Odblokuj' : 'Zablokuj' }}
          </button>

          <button class="btn btn-sm btn-outline-danger" @click="deleteUser(u)">
            Usuń
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination-footer">
      <button class="btn btn-sm btn-outline-light" :disabled="pageInternal <= 1" @click="prevPage">
        Poprzednia
      </button>

      <span class="page-info">Strona {{ pageInternal }} z {{ totalPages }}</span>

      <button class="btn btn-sm btn-outline-light" :disabled="pageInternal >= totalPages" @click="nextPage">
        Następna
      </button>
    </div>

    <div v-if="error" class="alert alert-danger mt-2">{{ error }}</div>

    <div v-if="confirmRoleModal.open" class="modal-overlay" @click.self="closeRoleModal">
      <div class="modal-box">
        <h3>Potwierdzenie zmiany roli</h3>

        <p>
          Czy na pewno chcesz zmienić rolę użytkownika
          <b>{{ confirmRoleModal.user?.username }}</b>
          na <b>{{ confirmRoleModal.newRole }}</b>?
        </p>

        <div class="d-flex justify-content-end gap-2 mt-3">
          <button class="btn btn-secondary" @click="closeRoleModal">Anuluj</button>
          <button class="btn btn-primary" @click="confirmRoleChange">Zmień</button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  search: String,
  sortField: String,
  sortDir: String,
  page: Number,
  pageSize: Number
})

const emit = defineEmits(['update:page'])
const auth = useAuthStore()

const allUsers = ref([])
const error = ref(null)
const loading = ref(false)

const pageInternal = ref(props.page)
watch(() => props.page, v => pageInternal.value = v)

const confirmRoleModal = ref({
  open: false,
  user: null,
  newRole: null
})

function changeSort(field) {}

async function loadUsers() {
  loading.value = true
  error.value = null

  try {
    axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

    const resp = await axios.get('/admin/users')
    const data = Array.isArray(resp.data) ? resp.data : resp.data.content || []

    allUsers.value = data.map(u => ({
      id: u.id,
      username: u.username,
      email: u.email,
      role: u.role,
      locked: u.isLocked ?? u.locked ?? false
    }))
  } catch (e) {
    error.value = 'Błąd pobierania użytkowników'
  } finally {
    loading.value = false
  }
}

const filteredAndSortedUsers = computed(() => {
  let list = [...allUsers.value]

  if (props.search) {
    const q = props.search.toLowerCase()
    list = list.filter(u =>
        u.username.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q)
    )
  }

  const field = props.sortField
  const dir = props.sortDir === 'desc' ? -1 : 1

  return list.sort((a, b) => {
    const va = a[field]
    const vb = b[field]
    if (typeof va === 'number') return (va - vb) * dir
    return String(va).localeCompare(String(vb)) * dir
  })
})

const totalPages = computed(() =>
    Math.max(1, Math.ceil(filteredAndSortedUsers.value.length / props.pageSize))
)

const pagedUsers = computed(() => {
  const start = (pageInternal.value - 1) * props.pageSize
  return filteredAndSortedUsers.value.slice(start, start + props.pageSize)
})

function prevPage() {
  if (pageInternal.value > 1) {
    const newPage = pageInternal.value - 1
    pageInternal.value = newPage
    emit('update:page', newPage)
  }
}

function nextPage() {
  if (pageInternal.value < totalPages.value) {
    const newPage = pageInternal.value + 1
    pageInternal.value = newPage
    emit('update:page', newPage)
  }
}

async function toggleLock(user) {
  const newStatus = !user.locked
  try {
    axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`
    await axios.put(`/admin/users/${user.id}/lock`, null, { params: { status: newStatus } })
    user.locked = newStatus
  } catch (_) {}
}

function onRoleChange(user, event) {
  confirmRoleModal.value = {
    open: true,
    user,
    newRole: event.target.value
  }
}

function closeRoleModal() {
  confirmRoleModal.value = { open: false, user: null, newRole: null }
}

async function confirmRoleChange() {
  const { user, newRole } = confirmRoleModal.value

  try {
    axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

    await axios.patch(
        `/admin/users/${user.id}/role`,
        null,
        { params: { role: newRole } }
    )

    user.role = newRole
  } catch (e) {
    error.value = 'Nie udało się zmienić roli użytkownika'
  }

  closeRoleModal()
}


onMounted(loadUsers)
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

.table td, .table th {
  vertical-align: middle;
}

.lock-btn {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  width: 90px;
  border: 1px solid;
  transition: 0.15s ease-in-out;
}

.lock {
  color: #ff6b6b;
  border-color: #ff6b6b;
}

.lock:hover {
  background: rgba(255, 100, 100, 0.2);
}

.unlock {
  color: #4cd964;
  border-color: #4cd964;
}

.unlock:hover {
  background: rgba(80, 220, 120, 0.2);
}

.role-select-wrapper {
  position: relative;
  width: 120px;
}

.role-select {
  width: 100%;
  padding: 4px 28px 4px 10px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  border-radius: 6px;
  appearance: none;
  cursor: pointer;
}

.role-select-wrapper::after {
  content: "▾";
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #aaa;
  font-size: 12px;
}


.role-select option {
  background: #1a1a1a !important;
  color: white !important;
}

@-moz-document url-prefix() {
  .role-select option {
    background: #1a1a1a !important;
    color: white !important;
  }
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  backdrop-filter: blur(6px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-box {
  background: rgba(25,25,25,0.95);
  padding: 20px;
  width: 360px;
  border-radius: 10px;
  color: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.4);
}

</style>








