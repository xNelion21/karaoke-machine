describe('Test Aplikacji Singly', () => {
    it('Powinien zarejestrować nowego użytkownika (Unikalnego)', () => {
        // 1. Generujemy unikalne dane
        const timestamp = Date.now()
        const uniqueUsername = `User_${timestamp}`
        const uniqueEmail = `user${timestamp}@test.com`
        const password = 'SuperHaslo123!'

        // 2. Wchodzimy na stronę i klikamy przycisk Rejestracji
        cy.visit('http://localhost:5173')
        cy.contains('Załóż konto').click()

        // 3. Wypełniamy formularz

        // Login / Nazwa użytkownika
        cy.get('input').eq(0).type(uniqueUsername)

        // Email
        cy.get('input[type="email"]').type(uniqueEmail)

        // Hasło
        cy.get('input[type="password"]').type(password)

        // 4. Klikamy przycisk wysyłania formularza
        cy.get('button').contains(/Zarejestruj|Dołącz/).click()

        // 5. Przekierowało na login
        cy.url().should('include', '/login')

        // 6. Jesteśmy na stronie logowania. Wpisujemy ten sam login, który wygenerowaliśmy wyżej
        cy.get('input').first().type(uniqueUsername)

        // 7. Wpisujemy to samo hasło
        cy.get('input[type="password"]').type(password)

        // 8. Klikamy przycisk logowania
        cy.get('button').contains('Zaloguj').click()

        cy.contains('Ulubione', { timeout: 10000 }).should('be.visible')

        // 10. Znajdź wyszukiwarkę i wpisz "Azizam"
        cy.get('input[placeholder="Wpisz tytuł..."]').type('Azizam')

        // 11. Kliknij w piosenkę na liście wyników
        cy.contains('Azizam', { timeout: 10000 }).should('be.visible').click()

        // 12. KLIKNIJ RAZ: Dodaj do ulubionych
        cy.get('.player-block .bi-heart')
            .should('be.visible')
            .first()
            .click()

        // 13. Sprawdź, czy serce się wypełniło
        cy.get('.player-block .bi-heart-fill', { timeout: 10000 }).should('be.visible')

        // 14. Sprawdź, czy piosenka jest na liście po lewej
        cy.contains('.sidebar-song-item', 'Azizam').should('be.visible')
    })
});