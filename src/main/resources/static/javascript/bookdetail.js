function addBookToBookshelf() {
    const bookshelfId = document.getElementById('bookshelfSelect').value;
    const bookID = document.getElementById('add-to-collection-btn').getAttribute('data-bookid');
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