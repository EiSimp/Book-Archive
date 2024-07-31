document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');
    console.log(bookID);

    fetchBookshelvesContainingBook(bookID);

    if (!bookID) {
        console.error("No book ID provided in the URL");
        return;
    }

    fetchBookDetails(bookID);

    document.getElementById("add-comment-btn").addEventListener("click", function () {
        document.getElementById("commentModal").style.display = "flex";
    });

    document.getElementById("add-read-btn").addEventListener("click", function() {
        addRead();
    });

    document.getElementById("add-reading-btn").addEventListener("click", function() {
        addReading();
    });

    document.getElementById("add-toRead-btn").addEventListener("click", function() {
        addToRead();
    })

    document.getElementById("add-to-collection-btn").addEventListener("click", function () {
        document.getElementById("bookshelfModal").style.display = "flex";
    });


});

function fetchBookDetails(bookID) {
    fetch(`/api/bookdetails/${bookID}`)
        .then(response => response.json())
        .then(book => {
            document.getElementById("book-title").innerText = book.title;
            document.getElementById("book-author").innerText = book.author;
            document.getElementById("book-publishedDate").innerText = book.publishedDate;
            document.getElementById("book-publisher").innerText = book.publisher;
            document.getElementById("book-isbn").innerText = book.isbn;
            document.getElementById("book-category").innerText = book.category;
            document.getElementById("book-description").innerHTML = book.description;
            document.getElementById("book-thumbnail").style.backgroundImage = `url(${book.biggerThumbnailUrl})`;
            document.getElementById("add-to-collection-btn").setAttribute('data-bookid', book.googleBookId);
            document.getElementById("book-authorDescription").innerText = book.authorDescription || 'No description available';
            fetchAuthorBio(book.author);
            document.getElementById("book-avgRate").innerText = book.averageRating;

            //Update the title
            document.title = `PagePals: ${book.title}`;
        })
        .catch(error => {
            console.error("Error fetching book details: ", error);
            alert('Failed to fetch book details.');
        });
}

function fetchAuthorBio(authorName) {
    console.log(authorName);
    const searchUrl = `https://openlibrary.org/search/authors.json?q=${encodeURIComponent(authorName)}`;
    fetch(searchUrl)
        .then(response => response.json())
        .then(data => {
            if (data.docs && data.docs.length > 0) {
                const authorId = data.docs[0].key;
                const authorUrl = `https://openlibrary.org/authors/${authorId}.json`;
                fetch(authorUrl)
                    .then(response => response.json())
                    .then(authorData => {
                        let bio = '';
                        if (authorData.bio) {
                            if (typeof authorData.bio === 'string') {
                                bio = authorData.bio;
                            } else if (authorData.bio.value) {
                                bio = authorData.bio.value;
                            }
                        }
                        document.getElementById("book-authorDescription").innerText = bio;
                    })
                    .catch(error => {
                        console.error("Error fetching author bio: ", error);
                        document.getElementById("book-authorDescription").innerText = '';
                    });
            } else {
                document.getElementById("book-authorDescription").innerText = '';
            }
        })
        .catch(error => {
            console.error("Error searching for author: ", error);
            document.getElementById("book-authorDescription").innerText = '';
        });
}



function addBookToBookshelf() {
    const bookshelfId = document.getElementById('bookshelfSelect').value;
    const urlParams = new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');
    console.log("Book ID:", bookID); // Add this line for debugging
    if (!bookshelfId) {
        alert('Please select a bookshelf.');
        return;
    }
    const book = {
        googleBookId: bookID,
        title: document.getElementById('book-title').innerText,
        author: document.getElementById('book-author').innerText,
        publisher: document.getElementById('book-publisher').innerText,
        thumbnailUrl: document.getElementById('book-thumbnail').src,
        biggerThumbnailUrl: document.getElementById('book-thumbnail').src,
        averageRating: document.getElementById('book-avgRate').innerText,
        publishedDate: document.getElementById('book-publishedDate').innerText,
        isbn: document.getElementById('book-isbn').innerText,
        category: document.getElementById('book-category').innerText,
        description: document.getElementById('book-description').innerText,
        authorDescription: document.getElementById('book-authorDescription').innerText
    };

    const csrf = getCsrfToken();

    // Fetch the full book details
    fetch(`/api/bookdetails/${bookID}`)
        .then(response => response.json())
        .then(book => {
            $.ajax({
                type: 'POST',
                url: `/bookshelf-items/add?bookshelfId=${bookshelfId}`,
                contentType: 'application/json',
                headers: {
                    [csrf.header]: csrf.token
                },
                data: JSON.stringify(book),
                success: function (response) {
                    alert('Book added to collection successfully!');
                    $('#bookshelfModal').modal('hide');
                },
                error: function (xhr) {
                    if (xhr.status === 409) {
                        alert('Book already exists in the bookshelf.');
                    } else {
                        console.error("Error adding book to collection: ", xhr);
                        alert('Failed to add book to collection.');
                    }
                }
            });
        })
        .catch(error => {
            console.error("Error fetching book details: ", error);
            alert('Failed to fetch book details.');
        });
}

function getCsrfToken() {
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}


function addRead() {
    //TODO: Make it actually add the book

    document.getElementById("add-read-img").src = "/images/checked.png";
}
function addReading() {
    //TODO:
    document.getElementById("add-reading-img").src = "/images/checked.png";
}
function addComment() {
    //TODO:
}
function addToRead() {
    document.getElementById("add-toRead-img").src = "/images/checked.png";
}
function rateBook() {
    //TODO:
}

// For displaying thumbnails on the collection card
function fetchAndSetThumbnails(bookshelfId) {
    fetch(`/bookshelves/${bookshelfId}/thumbnails`)
        .then(response => response.json())
        .then(thumbnails => {
            thumbnails.forEach((thumbnail, index) => {
                const thumbnailElenemt = document.getElementById(`thumbnail-${index}-${bookshelfId}`);
                if (thumbnailElenemt) {
                    thumbnailElenemt.style.backgroundImage =`url(${thumbnail})`;
                }
            });
    })
    .catch(error => console.error('Error fetching bookshelf thumbnails: ', error));
}

function fetchBookshelvesContainingBook(bookId) {
    const userId = document.querySelector('meta[name="user-id"]').getAttribute('content');
    const csrf = getCsrfToken();

    fetch(`/bookshelves/byBook/${bookId}?userID=${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        }
    })
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data)) {
                console.error('Response data is not an array: ',data);
                return;
            }

            const bookshelfUL = document.getElementById('collection-cardlist');
            bookshelfUL.innerHTML = ''; // Clear the collection div
            data.forEach(bookshelf => {
                // Create a div element for each bookshelf
                const bookshelfElement = document.createElement('li');
                bookshelfElement.className = 'collection-li';

                // Create an anchor element for the bookshelf
                const anchorElement = document.createElement('a');
                anchorElement.className = 'collection-detail-link';
                anchorElement.href = `/bookshelf/details/${bookshelf.id}`;

                // Create a div element for the thumbnails holder
                const collectionCard = document.createElement("div");
                collectionCard.className = "collection-card";

                // Create a div element for the card thumbnail holder
                const cardThumbnailHolder = document.createElement("div");
                cardThumbnailHolder.className = "collection-thumbnail-holder";
                collectionCard.appendChild(cardThumbnailHolder);

                // Create and append thumbnail images
                for (let i = 0; i < 5; i++) {
                    const thumbnailS = document.createElement("div");
                    thumbnailS.classList.add("thumbnail-s", "ts" + i);
                    const thumbnailImageS = document.createElement("div");
                    thumbnailImageS.className = "thumbnail-image-s";
                    thumbnailImageS.id = `thumbnail-${i}-${bookshelf.id}`;

                    thumbnailS.appendChild(thumbnailImageS);
                    cardThumbnailHolder.appendChild(thumbnailS);
                }

                // Create a div element for the card description
                const cardDescription = document.createElement("div");
                cardDescription.className = "card-description";

                // Create an unordered list for the card description list items
                const cardDescriptionList = document.createElement("ul");
                cardDescriptionList.className = "card-description-li";

                // Create and append the title list item
                const titleListItem = document.createElement("li");
                titleListItem.className = "collection-title";
                titleListItem.innerText = bookshelf.name;

                // Create and append the number of books list item
                const numOfBooksListItem = document.createElement("li");
                numOfBooksListItem.className = "card-numOfBooks";
                numOfBooksListItem.innerText = `${bookshelf.books} Books`;

                // Append list items to the card description list
                cardDescriptionList.appendChild(titleListItem);
                cardDescriptionList.appendChild(numOfBooksListItem);

                // Create lock element if the bookshelf is locked
                if (bookshelf.isSecret) {
                    const lockElement = document.createElement("li");
                    lockElement.className="card-lock";
                    lockElement.innerText = "🔒";
                    cardDescriptionList.appendChild(lockElement);
                }


                // Create id holder
                const idHolder = document.createElement('li');
                idHolder.style.setProperty('display','none');
                idHolder.className = 'bookshelfId';
                idHolder.innerText = bookshelf.id;

                cardDescriptionList.appendChild(idHolder);

                // Append the card description list to the card description div
                cardDescription.appendChild(cardDescriptionList);

                // Append the collection card to the anchor element
                anchorElement.appendChild(collectionCard);

                // Append the card description to the anchorElement
                anchorElement.appendChild(cardDescription);

                // Append the anchor element to the list item
                bookshelfElement.appendChild(anchorElement);

                // Append the bookshelf element to the bookshelf ul
                bookshelfUL.appendChild(bookshelfElement);

                fetchAndSetThumbnails(bookshelf.id);
            });
        })
        .catch(error => console.error('Error fetching bookshelves:', error));
}
