<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="text-center mb-4">
        <h1 class="h3 mb-3 fw-bold">{{ $t('auth.register_header') }}</h1>
        <p class="text-muted-dark">{{ $t('auth.register_sub') }}</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-floating mb-3">
          <input v-model="username" type="text" class="form-control" id="floatingUsername" placeholder="Username" required>
          <label for="floatingUsername">{{ $t('auth.username') }}</label>
        </div>

        <div class="form-floating mb-3">
          <input v-model="email" type="email" class="form-control" id="floatingEmail" placeholder="name@example.com" required>
          <label for="floatingEmail">{{ $t('auth.email') }}</label>
        </div>
        <div class="form-floating mb-3">
          <input v-model="password" type="password" class="form-control" id="floatingPassword" placeholder="Password" required>
          <label for="floatingPassword">{{ $t('auth.password') }}</label>
        </div>

        <div v-if="errorMessage" class="alert alert-danger mt-3">{{ errorMessage }}</div>
        <div v-if="successMessage" class="alert alert-success mt-3">{{ successMessage }}</div>

        <button class="w-100 btn btn-lg btn-primary mt-4" type="submit">{{ $t('auth.btn_register') }}</button>
      </form>

      <div class="divider my-4">{{ $t('auth.or') }}</div>

      <button @click="handleFacebookLogin" class="w-100 btn btn-lg btn-facebook" type="button">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" class="me-2"><path d="M9 8h-3v4h3v12h5v-12h3.642l.358-4h-4v-1.667c0-.955.192-1.333 1.115-1.333h2.885v-5h-3.808c-3.596 0-5.192 1.583-5.192 4.615v2.385z"/></svg>
        {{ $t('auth.btn_facebook') }}
      </button>

      <div class="mt-4 text-center">
        <p class="text-muted-dark">{{ $t('auth.has_account') }}
          <router-link to="/login">{{ $t('auth.btn_login') }}</router-link>
        </p>
      </div>
    </div>
  </div>
</template>
<script setup>

import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {useAuthStore} from "@/stores/auth.js";
import {storeToRefs} from "pinia";

const username = ref('')
const email = ref('')
const password = ref('')
const router = useRouter()
const authStore = useAuthStore()

const { errorMessage, successMessage } = storeToRefs(authStore)

const handleRegister = async () => {
  const success = await authStore.register({
    username: username.value,
    email: email.value,
    password: password.value,
  })

  if (success) {
    router.push('/login')
  }
}

const handleFacebookLogin = () => {
  if (!window.FB) {
    console.error("Facebook SDK nie jest załadowane.");
    return;
  }

  window.FB.login((response) => {
    if (response.authResponse) {
      window.FB.api('/me', { fields: 'id,name,email,picture' }, async (userData) => {
        console.log('Dane z FB:', userData);

        const success = await authStore.loginWithFacebook(userData);

        if (success) {
          router.push('/app');
        }
      });
    } else {
      console.log('Użytkownik anulował logowanie lub nie autoryzował aplikacji.');
    }
  }, { scope: 'public_profile,email' });
}

</script>

<style scoped>
.auth-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #181818 0%, #090923 100%);
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 30px;

  background: rgba(50, 50, 50, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);

  border-radius: 15px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
  color: #E0E0E0;
}

.text-muted-dark {
  color: #888;
}

.form-control {
  background-color: #181818;
  color: #E0E0E0;
  border: 1px solid #444;
}

.form-control:focus {
  background-color: #181818;
  color: #E0E0E0;
  border-color: #6C63FF;
  box-shadow: 0 0 0 0.25rem rgba(108, 99, 255, 0.25);
}

.form-floating > label {
  color: #888;
}

.btn-primary {
  background-color: #6C63FF;
  border-color: #6C63FF;
  padding: 12px 35px;
  font-weight: bold;
  color: white;
  border-radius: 8px;
  transition: all 0.3s ease-in-out;
  box-shadow: 0 4px 15px rgba(108, 99, 255, 0.3);
}

.btn-primary:hover {
  background-color: #574feb;
  border-color: #574feb;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(108, 99, 255, 0.5);
}

.auth-card a {
  color: #6C63FF;
  text-decoration: none;
}

.auth-card a:hover {
  text-decoration: underline;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  color: #888;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #444;
}

.divider:not(:empty)::before {
  margin-right: .25em;
}

.divider:not(:empty)::after {
  margin-left: .25em;
}

.btn-facebook {
  background-color: #1877F2;
  border-color: #1877F2;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.btn-facebook:hover {
  background-color: #166fe5;
  border-color: #166fe5;
}
</style>