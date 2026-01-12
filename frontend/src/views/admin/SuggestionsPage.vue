<template>
  <div class="suggestions-page">
    <h1 class="admin-title">Sugestie zmian tekstu</h1>

    <div class="controls-row">
      <input
          v-model="search"
          class="form-control search-input"
          placeholder="Wyszukaj sugestię"
      />

      <select v-model="sortBy" class="form-select sort-select">
        <option value="id">ID</option>
        <option value="song">Piosenka</option>
        <option value="user">Użytkownik</option>
        <option value="status">Status</option>
      </select>

      <select v-model="sortDir" class="form-select sort-select">
        <option value="asc">Rosnąco</option>
        <option value="desc">Malejąco</option>
      </select>
    </div>

    <SuggestionTable
        :search="search"
        :sort-by="sortBy"
        :sort-dir="sortDir"
        :page="page"
        @page-change="page = $event"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SuggestionTable from '@/components/admin/SuggestionsTable.vue'

const search = ref('')
const sortBy = ref('id')
const sortDir = ref('asc')
const page = ref(1)
</script>

<style scoped>
.suggestions-page {
  position: relative;
  color: white;
  padding: 20px;
  font-family: 'Inter', sans-serif;
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


