document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('search-button').addEventListener('click', function (event) {
        event.preventDefault();
        const query = document.getElementById('search-field').value;
        if (query) {
            window.location.href = `/search?q=${encodeURIComponent(query)}`;
        }
    });
});
