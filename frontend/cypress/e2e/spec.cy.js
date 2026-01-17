describe('Test Aplikacji Singly', () => {
    it('Powinien zalogować się i wyświetlić polubione piosenki', () => {
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

        // 7. Sprawdzamy piosenkę
        cy.contains('Azizam').should('be.visible')
    })
});