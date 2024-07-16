function addBookToBookshelf() {
    const bookshelfId = document.getElementById('bookshelfSelect').value;
    const bookID = document.getElementById('add-to-collection-btn').getAttribute('data-bookid');
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
        averageRating: 0.0,
        publishedDate: document.getElementById('book-publishedDate').innerText,
        isbn: document.getElementById('book-isbn').innerText,
        category: document.getElementById('book-category').innerText,
        description: document.getElementById('book-description').innerText,
        authorDescription: ''
    };

    const csrf = getCsrfToken();

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
        error: function (error) {
            console.error("Error adding book to collection: ", error);
            alert('Failed to add book to collection.');
        }
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


