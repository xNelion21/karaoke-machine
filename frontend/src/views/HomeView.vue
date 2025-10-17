<template>
  <div class="landing-page">
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
      <div class="container-fluid">
        <router-link to="/" class="navbar-brand mx-md-3">
          Singly
        </router-link>

        <div class="d-flex">
          <router-link to="/login" class="btn btn-outline-light me-3">
            Zaloguj się
          </router-link>
          <router-link to="/register" class="btn btn-primary">
            Zarejestruj się
          </router-link>
        </div>
      </div>
    </nav>

    <main class="hero-section text-white text-center">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-md-9">
            <h1 class="display-2 fw-bold hero-title">
              Twoja scena. Twoje słowa. Twój głos.
            </h1>
            <p class="lead mt-4 mb-5 hero-subtitle">
              Śpiewaj ulubione utwory z tekstem zsynchronizowanym w idealnym momencie. Dołącz do społeczności pasjonatów muzyki.
            </p>
            <div class="d-flex justify-content-center gap-3">
              <router-link to="/register" class="btn btn-primary btn-lg cta-button">
                Dołącz za darmo
              </router-link>
              <!-- Tymczasowy przycisk do nowej strony -->
              <router-link to="/app" class="btn btn-outline-light btn-lg">
                Sprawdź aplikację
                (tymczasowy przysisk)
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </main>

    <footer class="footer d-flex flex-wrap justify-content-between align-items-center py-3 px-4">
      <div class="col-md-4 d-flex align-items-center">
        <span class="mb-3 mb-md-0 fw-bold">© 2025 Singly</span>
      </div>

      <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
        <li class="ms-3">
          <a href="https://x.com" target="_blank">
            <i class="bi bi-twitter-x"></i>
          </a>
        </li>
        <li class="ms-3">
          <a href="https://instagram.com" target="_blank">
            <i class="bi bi-instagram"></i>
          </a>
        </li>
        <li class="ms-3">
          <a href="https://facebook.com" target="_blank">
            <i class="bi bi-facebook"></i>
          </a>
        </li>
      </ul>
    </footer>
  </div>
</template>

<script setup>

</script>

<style scoped>
.landing-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  color: #f0f0f0;
  background: linear-gradient(135deg, #181818 0%, #121020 100%);
  overflow-x: hidden;
}

.navbar {
  background: rgba(24, 24, 24, 0.6);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.navbar-brand {
  font-weight: bold;
  color: #ffffff;
  font-size: 1.5rem;
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
  padding: 120px 0;
}

.hero-title {
  background: -webkit-linear-gradient(45deg, #6C63FF, #a9a5ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 2px 2px 15px rgba(0,0,0,0.3);
  padding: 10px;
}

.hero-subtitle {
  color: #aab2c2;
  font-size: 1.5rem;
}

.btn-outline-light {
  padding: 12px 35px;
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

.footer {
  background-color: rgba(17, 17, 19, 0.27);
  color: rgba(170, 178, 194, 0.71);
}
.footer .nav a {
  color: rgba(170, 178, 194, 0.75);
  font-size: 1rem;
  transition: color 0.3s ease;
}
.footer .nav a:hover {
  color: #6C63FF;
}
</style>