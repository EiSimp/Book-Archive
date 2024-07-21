document.addEventListener("DOMContentLoaded", function() {
    const urlParams =new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');

    if (!bookID) {
        console.error("No book ID provided in the URL");
        return;
    }

    fetchBookDetails(bookID);

    document.getElementById("add-to-collection-btn").addEventListener("click", function() {
        addBookToBookshelf(bookID);
    });
    
});

function fetchBookDetails(bookID) {
    fetch(`/api/bookdetails/${bookID}`)
    .then(response => response.json())
    .then(book => {
        document.getElementById("book-title").innerText = book.title;
        document.getElementById("book-author").innerText = book.author;
        document.getElementById("book-publishedDate"),innerText = book.publishedDate;
        document.getElementById("book-publisher").innerText = book.publisher;
        document.getElementById("book-isbn").innerText = book.isbn;
        document.getElementById("book-category").innerText = book.category;
        document.getElementById("book-description").innerHTML = book.description;
        document.getElementById("book-thumbnail").style.backgroundImage = `url(${book.biggerThumbnailUrl})`;
        document.getElementById("add-to-collection-btn").setAttribute('data-bookid', book.googleBookId);
        document.getElementById("book-authorDescription").innerText = book.authorDescription || 'No description available';

        //Update the title
        document.title = `PagePals: ${book.title}`;
        })
        .catch(error => {
            console.error("Error fetching book details: ", error);
            alert('Failed to fetch book details.');
        });
}


function addBookToBookshelf() {
    const bookshelfId = document.getElementById('bookshelfSelect').value;
    console.log("Book ID:", bookID); // Add this line for debugging

    if (!bookshelfId) {
        alert('Please select a bookshelf.');
        return;
    }

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