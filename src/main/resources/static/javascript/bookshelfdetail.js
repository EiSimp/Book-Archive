document.addEventListener("DOMContentLoaded", function () {
    const bookshelfId = document.getElementById("bookshelf-id").value;
    const currentUserId = getCurrentUserId();
    const csrf = getCsrfToken();

    fetch(`/bookshelves/${bookshelfId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        }
    })
        .then(response => response.json())
        .then(data => {
            const bookshelf = data.bookshelf;
            const items = data.items;

            if (!bookshelf) {
                throw new Error("Bookshelf not found");
            }

            // Display bookshelf info
            document.getElementById("bookshelf-name").innerText = "Collection:  " + bookshelf.name;
            document.getElementById("bookshelf-secret").innerText = bookshelf.isSecret ? '🔒' : '';
            
            // Update the title
            document.title = "PagePals: " + bookshelf.name;

            const bookList = document.getElementById("book-list");
            bookList.innerHTML = ''; // Clear existing content

            items.forEach(book => {
                const bookLi = document.createElement("li");
                bookLi.classList.add("card-li");

                const bookAnchor = document.createElement("a");
                bookAnchor.classList.add("detail-link");
                bookAnchor.href = `/bookdetail?id=${book.googleBookId}`;

                const bookCard = document.createElement("div");
                bookCard.classList.add("book-card");

                const cardThumbnailHolder = document.createElement("div");
                cardThumbnailHolder.classList.add("card-thumbnail-holder");

                const thumbnailM = document.createElement("div");
                thumbnailM.classList.add("thumbnail-m");

                const thumbnailImageM = document.createElement("div");
                thumbnailImageM.classList.add("thumbnail-image-m");

                if (book.thumbnail) {
                    thumbnailImageM.style.backgroundImage = `url(${book.thumbnail})`;
                    
                }

                thumbnailM.appendChild(thumbnailImageM);
                cardThumbnailHolder.appendChild(thumbnailM);
                bookCard.appendChild(cardThumbnailHolder);

                const cardDescription = document.createElement("div");
                cardDescription.classList.add("card-description");

                const cardDescriptionList = document.createElement("ul");
                cardDescriptionList.classList.add("card-description-li");

                const cardTitle = document.createElement("li");
                cardTitle.classList.add("card-title");
                cardTitle.innerText = book.title;

                const cardRate = document.createElement("li");
                cardRate.classList.add("card-rate");
                cardRate.innerText = book.userRating ? `Rated ★ ${book.userRating}` : `Avg ★ ${book.averageRating}`;

                cardDescriptionList.appendChild(cardTitle);
                cardDescriptionList.appendChild(cardRate);

                cardDescription.appendChild(cardDescriptionList);
                bookAnchor.appendChild(bookCard);
                bookAnchor.appendChild(cardDescription);
                bookLi.appendChild(bookAnchor);
                bookList.appendChild(bookLi);

                // Add delete button if user is owner of the bookshelf
                if (bookshelf.userId == currentUserId) {
                    const deleteButton = document.createElement("button");
                    deleteButton.innerText = "Delete";
                    deleteButton.classList.add("btn", "btn-danger", "delete-book-button");
                    deleteButton.addEventListener("click", function () {
                        event.preventDefault();
                        deleteBook(book.googleBookId);
                    });
                    bookCard.appendChild(deleteButton);
                }
            });

        })
        .catch(error => console.error('Error fetching bookshelf details: ', error));
});

function getCsrfToken() {
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}

function getCurrentUserId() {
    return document.querySelector('meta[name="user-id"]').getAttribute('content');
}

function deleteBook(bookId) {
    const bookshelfId = document.getElementById("bookshelf-id").value;
    const csrf = getCsrfToken();


    fetch(`/bookshelf-items/${bookshelfId}/${bookId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        }
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            alert("Failed to delete book");
        }
    })
    .catch(error => console.error('Error deleting book: ', error));
}
