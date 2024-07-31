document.addEventListener("DOMContentLoaded", function () {
    const bookClubId = getBookClubId();
    const currentUserId = getCurrentUserId();
    const csrf = getCsrfToken();

    fetch(`/bookclubs/${bookClubId}/bookshelf`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log('Fetched data:', data);  // Log fetched data

            const bookshelf = data.bookshelf;
            const items = data.items;

            if (!bookshelf) {
                throw new Error("Bookshelf not found");
            }

            // Display bookshelf info
            const bookList = document.getElementById("book-list");
            bookList.innerHTML = ''; // Clear existing content

            items.forEach(item => {
                console.log('Processing book:', item);  // Log each book being processed
                const book = item.book;  // Access the book property inside each item

                // Check if thumbnail exists
                console.log('Book Thumbnail:', book.thumbnailUrl);  // Log the thumbnail URL property

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

                if (book.thumbnailUrl) {  // Use the correct property name
                    thumbnailImageM.style.backgroundImage = `url(${book.thumbnailUrl})`;
                    thumbnailImageM.style.backgroundSize = 'cover';  // Ensure the image covers the container
                    thumbnailImageM.style.backgroundPosition = 'center';  // Center the image
                } else {
                    // If there is no thumbnail, set a default image or leave it empty
                    thumbnailImageM.style.backgroundImage = 'url(/path/to/default/image.png)';
                    thumbnailImageM.style.backgroundSize = 'cover';
                    thumbnailImageM.style.backgroundPosition = 'center';
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
                cardRate.innerText = item.userRating ? `Rated ★ ${item.userRating}` : `Avg ★ ${book.averageRating}`;

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
                    deleteButton.addEventListener("click", function (event) {
                        event.preventDefault();
                        deleteBook(book.googleBookId);
                    });
                    bookCard.appendChild(deleteButton);
                }
            });

        })
        .catch(error => {
            console.error('Error fetching bookshelf details: ', error);
            alert('Error fetching bookshelf details. Check console for more details.');
        });
});

function getCsrfToken() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}

function getCurrentUserId() {
    return document.getElementById('userId').value;
}

function getBookClubId() {
    return document.getElementById('bookClubId').value;
}

function deleteBook(bookId) {
    const bookClubId = getBookClubId();
    const csrf = getCsrfToken();

    fetch(`/bookshelf-items/${bookClubId}/${bookId}`, {
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
