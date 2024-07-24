document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');
    console.log(bookID);

    if (!bookID) {
        console.error("No book ID provided in the URL");
        return;
    }

    fetchBookDetails(bookID);

    document.getElementById("add-to-collection-btn").addEventListener("click", function () {
        document.getElementById("bookshelfModal").style.display = "flex";
    });

    const labels = document.querySelectorAll('.label_star');
    
    labels.forEach(label => {
        label.addEventListener('click', function() {
            addBookToDefaultBookshelf('Rating');
        });
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
                        console.log(xhr.status);
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

function addBookToDefaultBookshelf(name) {
    let bookshelfId;

    // Determine bookshelfId based on the name
    if (name === "Read") {
        bookshelfId = document.getElementById('readSelect').value;
    } else if (name === "Reading") {
        bookshelfId = document.getElementById('readingSelect').value;
    } else if (name === "To Read") {
        bookshelfId = document.getElementById('toReadSelect').value;
    } else if (name === "Rating") {
        // rating a book adds it to Read
        bookshelfId = document.getElementById('readSelect').value;
    } else {
        console.error("Unknown bookshelf name:", name);
        return; // Return early if the name doesn't match expected values
    }
    const urlParams = new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');
    console.log("Book ID:", bookID); // Add this line for debugging
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
                    let successMessage;
                    if (name === "Books-Read") {
                        successMessage = 'Book added to Read bookshelf!';
                    } else if (name === "Books-Reading") {
                        successMessage = 'Book added to Reading bookshelf!';
                    } else if (name === "Books-to-Read") {
                        successMessage = 'Book added to To Read bookshelf!';
                    } else if (name === "Rating") {
                        successMessage = 'Book rated and added to Read bookshelf!';
                    } else {
                        successMessage = 'Book added to collection successfully!';
                    }
                    alert(successMessage);
                    $('#bookshelfModal').modal('hide');
                },
                error: function (xhr) {
                    if (xhr.status === 409) {
                        if (name === "Rating") {
                            alert('Book rating updated successfully!\nBook already exists in the Read bookshelf.');
                        } else {
                            alert('Book already exists in the ' + name + ' bookshelf.\n');
                        }
                    } else {
                        console.error("Error adding book to collection: ", xhr);
                        console.log(xhr.status);
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