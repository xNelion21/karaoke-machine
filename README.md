# karaoke-machine
Projekt grupowy za zaliczenie przedmiotu 
etapy i podział ról: 

Etap 1: MVP  - Użytkownik może się zalogować/zarejestrować i zobaczyć prosty odtwarzacz piosenek z twardo zakodowanym tekstem
Backend (Roksana i Marcin)
Konfiguracja: Stworzenie projektu Spring Boot, połączenie z bazą MySQL.

Użytkownicy (Roksana): Stworzenie modeli (Entities) i repozytoriów dla Użytkownika i Roli (User, Role). Implementacja rejestracji/logowania (email/hasło). Zabezpieczenie API (np. JWT).

Piosenki (Marcin): Stworzenie modeli dla Piosenki, Autora, Kategorii. Stworzenie prostego API do dodawania/pobierania piosenki (na razie bez znaczników czasu, tylko tytuł, autor, URL).

Frontend (Mikołaj i Kamil Z)
Konfiguracja: Stworzenie projektu Vue, konfiguracja routingu (Vue Router).
Mikołaj już masz doświadczenie, wiec pokażesz kamilowi jak działa vue, jaka jest roznica miedzy widokami a komponentami i jak wstrzykiwać jedno do drugiego 

Uwierzytelnianie (Mikołaj): Implementacja widoków Rejestracji i Logowania. Implementacja logiki wysyłania danych do API backendu.

Podstawowy Widok (Kamil Z.): Stworzenie komponentu wyszukiwania (na razie tylko pole tekstowe). Stworzenie widoku odtwarzacza z miejscem na wideo i tekst piosenki.

Testowanie (Kamil K.)
Testy jednostkowe API (rejestracja, logowanie, dodawanie piosenki).
Testy  procesu rejestracji i logowania na frontendzie.
Dokumentacja tych testów 


Etap 2: Główne Funkcjonalności
Cel: Użytkownik może wyszukać i odtworzyć piosenkę z synchronizowanym tekstem. Administrator ma podstawowe narzędzia.

Backend (Roksana i Marcin)
Wyszukiwanie (Marcin): Rozbudowa API wyszukiwania o filtry: tytuł, autor, kategoria, fragment tekstu (wymaga logicznego przeszukiwania tekstów piosenek).

Znaczniki Czasu (Marcin): Modyfikacja modelu Piosenki o strukturę do przechowywania tekstu i znaczników czasowych (np. JSON w bazie lub osobna tabela: line_number, timestamp_start, text).

Zarządzanie (Roksana): Stworzenie podstawowego API dla administratora: zarządzanie użytkownikami (blokowanie/odblokowywanie). Wdrożenie autoryzacji opartej na rolach (ROLE_USER, ROLE_ADMIN).

Rejestracja Facebook (Roksana): Dodanie integracji z Facebookiem (OAuth2/OpenID Connect) do rejestracji.

Frontend (Mikołaj & Kamil Z.)
Wyszukiwarka (Kamil Z.): Podłączenie widoku wyszukiwania do nowego API, wyświetlanie listy wyników.

Odtwarzacz (Mikołaj): Implementacja zewnętrznego odtwarzacza (np. komponent Vue/biblioteka do obsługi iFrame Youtube).

Synchronizacja Tekstu (Mikołaj): Logika pobierania tekstu ze znacznikami czasu. Implementacja mechanizmu, który podświetla/wyświetla aktualny wiersz na podstawie czasu odtwarzania.

Testowanie (Kamil K.)
Testowanie wydajności i dokładności wyszukiwania.
Testowanie synchronizacji tekstu z wideo.
Testy funkcjonalne logowania przez Facebooka.

Etap 3: Udoskonalenia i Zarządzanie
Backend (Roksana & Marcin)
Propozycje (Marcin): Stworzenie modelu Propozycji Zmiany Tekstu (np. SuggestedLyricChange) z polami: user_id, song_id, original_text, new_text, status (PENDING, ACCEPTED, REJECTED). API do dodawania propozycji.

Pełne Zarządzanie (Roksana): Rozbudowa paneli administracyjnych: zarządzanie autorami i kategoriami. API do akceptowania/odrzucania propozycji (aktualizacja głównego tekstu piosenki po akceptacji).

Frontend (Mikołaj & Kamil Z.)
Edycja (Kamil Z.): Interfejs do proponowania zmian (np. przycisk "Zaproponuj Zmianę Tekstu" w widoku piosenki).

Panel Admina (Mikołaj): Stworzenie podstawowych widoków dla administratora: Lista użytkowników, Zarządzanie piosenkami (tekst, źródło), Widok propozycji użytkowników.

Testowanie (Kamil K.)
Testowanie całego cyklu propozycji (użytkownik proponuje → admin akceptuje → tekst się zmienia).
Testy bezpieczeństwa i znow dokumentacja
