document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('search-button').addEventListener('click', function (event) {
        event.preventDefault();
        const query = document.getElementById('search-field').value;
        if (query) {
            window.location.href = `/search?q=${encodeURIComponent(query)}&page=1`;
        }
    });

    document.querySelectorAll('.pagination-link').forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            const query = new URLSearchParams(window.location.search).get('q');
            const page = this.getAttribute('data-page');
            if (query && page) {
                window.location.href = `/search?q=${encodeURIComponent(query)}&page=${page}`;
            }
        });
    });
});
