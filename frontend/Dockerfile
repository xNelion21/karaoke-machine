# frontend/Dockerfile

# --- STAGE 1: ETAP BUDOWANIA (npm install & npm run build) ---
FROM node:20-alpine AS build-stage
WORKDIR /app

# Kopiuje pliki zależności
COPY package.json package-lock.json ./

# Instaluje wszystkie zależności
RUN npm install

# Kopiuje resztę kodu
COPY . .

# Buduje aplikację Vue do statycznych plików HTML/CSS/JS
RUN npm run build


# --- STAGE 2: ETAP SERWOWANIA (Minimalny obraz Nginx) ---
FROM nginx:alpine AS production-stage

# Usuwa domyślne pliki Nginxa
RUN rm -rf /usr/share/nginx/html/*

# Kopiuje zbudowaną aplikację z ETAPU BUDOWANIA do katalogu serwowania Nginxa
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Kopiuje plik konfiguracyjny Nginx (jeśli go używasz). Na razie używamy domyślnego.
# Jeśli będzie potrzebna konfiguracja (np. routing), dodamy ją później.

# Domyślny port Nginx to 80.
EXPOSE 80

# Kontener automatycznie uruchamia Nginx
CMD ["nginx", "-g", "daemon off;"]