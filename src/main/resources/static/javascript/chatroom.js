document.addEventListener("DOMContentLoaded", function () {

    const messages = document.querySelectorAll('.message');

    messages.forEach(message => {
        message.addEventListener('click', () => {
            message.classList.toggle('show-reactions');
        });
    });

    document.getElementById('search-icon').addEventListener('click', function() {
        const searchInput = document.getElementById('search-input');
    if (searchInput.style.display === 'none' || searchInput.style.display === '') {
        searchInput.style.display = 'block';
        searchInput.focus();
    } else {
        searchInput.style.display = 'none';
    }
    });
});