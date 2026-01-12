<template>
  <div class="users-page">
    <h1 class="admin-title">Użytkownicy</h1>

    <div class="controls-row">
      <input
          v-model="search"
          class="form-control search-input"
          placeholder="Wyszukaj użytkownika"
      />

      <select v-model="sortField" class="form-select sort-select">
        <option value="id">ID</option>
        <option value="username">Username</option>
        <option value="email">Email</option>
        <option value="role">Rola</option>
      </select>

      <select v-model="sortDir" class="form-select sort-select">
        <option value="asc">Rosnąco</option>
        <option value="desc">Malejąco</option>
      </select>
    </div>

    <UsersTable
        :search="search"
        :sort-field="sortField"
        :sort-dir="sortDir"
        :page="page"
        :page-size="pageSize"
        @update:page="page = $event"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import UsersTable from '@/components/admin/UsersTable.vue'

const search = ref('')
const sortField = ref('id')
const sortDir = ref('asc')
const page = ref(1)
const pageSize = ref(10)
</script>

<style scoped>
.users-page {
  position: relative;
  color: white;
  padding: 20px;
}

.admin-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.controls-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.sort-select {
  width: 160px;
}
</style>



