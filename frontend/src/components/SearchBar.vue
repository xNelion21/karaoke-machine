<template>
  <div class="search-container mb-4">
    <input
        v-model="searchQuery"
        type="text"
        class="form-control search-input"
        placeholder="Wyszukaj piosenkę..."
    />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  modelValue: String
})


const searchQuery = computed({
  get: () => props.modelValue,
  set: val => emit('update:modelValue', val)
})
</script>

<style scoped>
.search-container {
  display: flex;
  justify-content: center;
}

.search-input {
  color: #ffffff;
  background-color: rgba(30,30,30,0.7);
  border: 1px solid #6C63FF;
  border-radius: 25px;
  padding: 12px 20px;
  width: 100%;
  max-width: 600px;
  font-size: 1.1rem;
}

.search-input::placeholder {
  color: #ccc;
  opacity: 1;
}

.search-input:focus {
  outline: none;
  border-color: #574feb;
  box-shadow: 0 0 0 0.25rem rgba(108, 99, 255, 0.3);
}
</style>

