describe('Test Aplikacji Singly', () => {
    it('Powinien zalogować się, wybrać piosenkę Modelki i wyświetlić tekst', () => {
        // 1. Wejdź na stronę główną
        cy.visit('http://localhost:5173')

        // 2. Kliknij "Zaloguj się" na stronie startowej
        cy.contains('Zaloguj się').click()

        // 3. Wpisz login/email
        cy.get('input').first().type('testuser')

        // 4. Wpisz hasło
        cy.get('input[type="password"]').type('password123')

        // 5. Kliknij przycisk logowania
        cy.get('button').contains('Zaloguj').click()

        // 6. Poczekaj na załadowanie dashboardu
        cy.contains('Ulubione', { timeout: 10000 }).should('be.visible')

        // 7. Znajdź piosenkę na liście i kliknij
        cy.contains('.sidebar-song-item', 'Chyba').click()

        // 8. Czekamy, aż zniknie animacja ładowania tekstu
        cy.get('.lyrics-display .loading', { timeout: 20000 }).should('not.exist')

        // 9. Sprawdzamy, czy tekst faktycznie się wyświetlił
        cy.get('.teleprompter-content .line')
            .should('have.length.gt', 0)
            .and('be.visible')
    })
})