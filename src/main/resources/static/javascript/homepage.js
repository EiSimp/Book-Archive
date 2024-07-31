document.addEventListener('DOMContentLoaded', function () {
    const bookshelfLinks = document.querySelectorAll('.bookshelf-link');
    const bookList = document.getElementById('book-list');
    const pagination = document.getElementById('pagination');

    // Get initial bookshelf IDs from <option> elements
    const readSelect = document.getElementById('readSelect');
    const readingSelect = document.getElementById('readingSelect');
    const toReadSelect = document.getElementById('toReadSelect');

    const bookshelfIds = {
        read: readSelect ? readSelect.value : null,
        reading: readingSelect ? readingSelect.value : null,
        toRead: toReadSelect ? toReadSelect.value : null
    };

    // Default to 'Read' bookshelf if no other bookshelf is selected
    let currentBookshelfId = bookshelfIds.read;

    // Function to fetch and display books
    function fetchBooks(bookshelfId, page = 0, size = 6) {
        const csrf = getCsrfToken();

        fetch(`/bookshelves/${bookshelfId}/items?page=${page}&size=${size}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                [csrf.header]: csrf.token
            }
        })
        .then(response => response.json())
        .then(data => {
            // Clear current book list and pagination
            bookList.innerHTML = '';
            pagination.innerHTML = '';

            // Populate book list
            data.content.forEach(item => {
                const listItem = document.createElement('li');
                listItem.className = 'card-li';

                const bookAnchor = document.createElement('a');
                bookAnchor.classList.add('detail-link');
                bookAnchor.href = `/bookdetail?id=${item.googleBookId}`;

                const bookCard = document.createElement('div');
                bookCard.classList.add('book-card');

                const cardThumbnailHolder = document.createElement('div');
                cardThumbnailHolder.classList.add('card-thumbnail-holder');

                const thumbnailM = document.createElement('div');
                thumbnailM.classList.add('thumbnail-m');

                const thumbnailImageM = document.createElement('div');
                thumbnailImageM.classList.add('thumbnail-image-m');
                thumbnailImageM.style.backgroundImage = `url('${item.thumbnailUrl}')`;

                thumbnailM.appendChild(thumbnailImageM);
                cardThumbnailHolder.appendChild(thumbnailM);
                bookCard.appendChild(cardThumbnailHolder);

                const cardDescription = document.createElement('div');
                cardDescription.classList.add('card-description');

                const cardDescriptionList = document.createElement('ul');
                cardDescriptionList.classList.add('card-description-li');

                const cardTitle = document.createElement('li');
                cardTitle.classList.add('card-title');
                cardTitle.innerText = item.title;

                const cardRate = document.createElement('li');
                cardRate.classList.add('card-rate');
                cardRate.innerText = item.userRating ? `Rated ★ ${item.userRating}` : `Avg ★ ${item.averageRating}`;

                cardDescriptionList.appendChild(cardTitle);
                cardDescriptionList.appendChild(cardRate);

                cardDescription.appendChild(cardDescriptionList);
                bookAnchor.appendChild(bookCard);
                bookAnchor.appendChild(cardDescription);
                listItem.appendChild(bookAnchor);
                bookList.appendChild(listItem);

                // Always add delete button since the user is always the owner
                const deleteButton = document.createElement('button');
                deleteButton.innerText = 'Delete';
                deleteButton.classList.add('btn', 'btn-danger', 'delete-book-button');
                deleteButton.addEventListener('click', function (event) {
                    event.preventDefault();
                    deleteBook(item.googleBookId);
                });
                bookCard.appendChild(deleteButton);
            });

            // Populate pagination
            const currentPage = data.number;
            const totalPages = data.totalPages;

            if (currentPage > 0) {
                const prevLink = document.createElement('a');
                prevLink.href = '#';
                prevLink.className = 'pagination-link';
                prevLink.dataset.page = currentPage - 1;
                prevLink.textContent = ' < ';
                pagination.appendChild(prevLink);
            }

            for (let i = 0; i < totalPages; i++) {
                const pageLink = document.createElement('a');
                pageLink.href = '#';
                pageLink.className = `pagination-link ${i === currentPage ? 'active' : ''}`;
                pageLink.dataset.page = i;
                pageLink.textContent = i + 1;
                pagination.appendChild(pageLink);
            }

            if (currentPage < totalPages - 1) {
                const nextLink = document.createElement('a');
                nextLink.href = '#';
                nextLink.className = 'pagination-link';
                nextLink.dataset.page = currentPage + 1;
                nextLink.textContent = ' > ';
                pagination.appendChild(nextLink);
            }

            // Attach click handlers to pagination links
            document.querySelectorAll('.pagination-link').forEach(link => {
                link.addEventListener('click', function (event) {
                    event.preventDefault();
                    const page = this.dataset.page;
                    fetchBooks(currentBookshelfId, page);
                });
            });
        })
        .catch(error => console.error('Error fetching books:', error));
    }

    // Function to delete a book
    function deleteBook(bookId) {
        const csrf = getCsrfToken();

        fetch(`/bookshelf-items/${currentBookshelfId}/${bookId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [csrf.header]: csrf.token
            }
        })
        .then(response => {
            if (response.ok) {
                fetchBooks(currentBookshelfId); // Reload books after deletion
            } else {
                alert('Failed to delete book');
            }
        })
        .catch(error => console.error('Error deleting book:', error));
    }

    // Function to get CSRF token
    function getCsrfToken() {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        return {
            token: csrfToken,
            header: csrfHeader
        };
    }

    // Set up bookshelf link click handlers
    bookshelfLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            // Determine the bookshelf ID based on the link text
            switch (this.textContent.replace(/\s+/g, '')) {
                case '00Read':
                    currentBookshelfId = bookshelfIds.read;
                    break;
                case '00Reading':
                    currentBookshelfId = bookshelfIds.reading;
                    break;
                case '00ToRead':
                    currentBookshelfId = bookshelfIds.toRead;
                    break;
                default:
                    console.error('Unknown bookshelf type:', this.textContent.trim());
                    return;
            }

            fetchBooks(currentBookshelfId);
        });
    });
    

    // Handle initial fetch based on the default or selected bookshelf
    fetchBooks(currentBookshelfId);
});
