<template>
  <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand mx-md-3">Singly</router-link>

      <div class="d-flex align-items-center">
        <template v-if="!authStore.isAuthenticated">
          <button @click="goLogin" class="btn btn-outline-light me-3">
            Zaloguj się
          </button>
          <button @click="goRegister" class="btn btn-primary">
            Zarejestruj się
          </button>
        </template>

        <template v-else>
          <div class="nav-item dropdown" ref="profileDropdownRef">
            <a class="nav-link dropdown-toggle d-flex align-items-center"
               href="#"
               id="profileDropdown"
               role="button"
               @click="toggleProfileDropdown"
               :aria-expanded="isProfileDropdownOpen ? 'true' : 'false'">
              <i class="bi bi-person-circle fs-4 me-2"></i>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" :class="{ show: isProfileDropdownOpen }" aria-labelledby="profileDropdown">

              <li>
                <span class="dropdown-item-text fw-bold">Zalogowany jako: </span>
                <span class="dropdown-item-text fw-bold text-primary text-truncate">{{ authStore.username || 'Ładowanie...' }}</span>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <a class="dropdown-item text-danger fw-bold" href="#" @click.prevent="handleLogout">
                  <i class="bi bi-box-arrow-right me-2 bold"></i> Wyloguj się
                </a>
              </li>
            </ul>
          </div>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

defineProps({
  showSearch: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const authStore = useAuthStore()

const isProfileDropdownOpen = ref(false)
const profileDropdownRef = ref(null)

const toggleProfileDropdown = () => {
  isProfileDropdownOpen.value = !isProfileDropdownOpen.value
}

const closeProfileDropdown = () => {
  isProfileDropdownOpen.value = false
}

function goLogin() {
  router.push('/login')
}

function goRegister() {
  router.push('/register')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
  closeProfileDropdown()
}

const handleClickOutside = (event) => {
  if (profileDropdownRef.value && !profileDropdownRef.value.contains(event.target)) {
    closeProfileDropdown()
  }
}

onMounted(() => {
  if (authStore.token && !authStore.user) {
    authStore.fetchUser()
  }
  window.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.navbar {
  background: rgba(24, 24, 24, 0.6);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 1050;
}

.navbar-brand {
  font-weight: bold;
  color: #ffffff;
  font-size: 1.5rem;
}

.btn-outline-light {
  padding: 10px 35px;
  font-weight: bold;
}

.btn-primary {
  background-color: #6C63FF;
  border-color: #6C63FF;
  padding: 12px 35px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(108, 99, 255, 0.3);
}

.btn-primary:hover {
  background-color: #574feb;
  border-color: #574feb;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(108, 99, 255, 0.4);
}

.search-wrapper {
  display: flex;
  justify-content: center;
  width: 300px;
}

.nav-item.dropdown .nav-link {
  color: #E0E0E0 !important;
}
.nav-item.dropdown .nav-link:hover {
  color: #6C63FF !important;
}

.dropdown-menu {
  background-color: rgba(50, 50, 50, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  padding: 10px 0;

  position: absolute;
  top: 100%;
  left: auto;
  right: 0;
  margin-top: 5px;
  min-width: 180px;
}
.dropdown-item, .dropdown-header, .dropdown-item-text {
  color: #E0E0E0;
  padding: 8px 15px;
}
.dropdown-item:hover {
  background-color: rgba(108, 99, 255, 0.2);
  color: #6C63FF;
}
.dropdown-divider {
  border-top-color: rgba(255, 255, 255, 0.1);
  margin: 8px 0;
}
.text-primary {
  color: #6C63FF !important;
}
.text-danger {
  color: #ff6b6b !important;
}
</style>