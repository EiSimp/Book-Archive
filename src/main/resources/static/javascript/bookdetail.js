
document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const bookID = urlParams.get('id');
    console.log(bookID);

    if (!bookID) {
        console.error("No book ID provided in the URL");
        return;
    }

    fetchBookDetails(bookID);

    document.getElementById("add-comment-btn").addEventListener("click", function () {
        document.getElementById("commentModal").style.display = "flex";
    });

    document.getElementById("add-to-collection-btn").addEventListener("click", function () {
        document.getElementById("bookshelfModal").style.display = "flex";
    });

    const labels = document.querySelectorAll('.label_star');
    
    labels.forEach(label => {
        label.addEventListener('click', function() {
            rateBook(this);
        });
    });

    updateBookStatusByDefaultBookshelves(bookID);
    loadCurrentRating();

    loadComments();

    // load the comments
    // fetch(`/comments/book/${encodeURIComponent(googleBookId)}`)
    // .then(response => response.json())
    // .then(comments => {
    //     console.log(comments); // Check the format of the response
    //     if (Array.isArray(comments)) {
    //         const commentsContainer = document.getElementById('comments-container');
    //         commentsContainer.innerHTML = ''; // Clear existing content

    //         comments.forEach(comment => {
    //             // console.log('Comment Properties:', Object.keys(comment));
    //             // console.log('Comment:', comment);
    //             console.log(comment.comment.user);
    //             const commentCard = createCommentCard(comment);
    //             commentsContainer.appendChild(commentCard);
    //         });
    //     } else {
    //         console.error('Expected an array but received:', comments);
    //     }
    // })
    // .catch(error => console.error('Error fetching comments:', error));


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

function addRead() {
    addBookToDefaultBookshelf("Read");

    document.getElementById("add-read-img").src = "/images/checked.png";
}
function addReading() {
    addBookToDefaultBookshelf("Reading");
    document.getElementById("add-reading-img").src = "/images/checked.png";
}
function addComment() {
    //TODO:
}
function addToRead() {
    addBookToDefaultBookshelf("To Read");
    document.getElementById("add-toRead-img").src = "/images/checked.png";
}
function addWishlist() {
    addBookToDefaultBookshelf("Wishlist");
    document.getElementById("add-wishlist-img").src = "/images/checked.png";
}

function  rateBook(selectedLabel) {
    //TODO:
    addBookToDefaultBookshelf("Rating");
    const urlParams = new URLSearchParams(window.location.search);
    const csrf = getCsrfToken();

    const ratingValue = selectedLabel.getAttribute('title');
    // rated books should be added by default to read bookshelf
    const bookshelfId = document.getElementById('readSelect').value;
    const googleBookId =  urlParams.get('id');
    
    fetch(`/bookshelf-items/rating?bookshelfId=${bookshelfId}&googleBookId=${googleBookId}&rating=${ratingValue}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(updatedItem => {
        console.log('Rating updated successfully:', updatedItem);
        // Optionally update the UI to reflect the changes
        
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function updateBookStatusByDefaultBookshelves(bookID) {
    // Define an array of option IDs and their corresponding image IDs
    const bookshelves = [
        { optionId: 'readSelect', imgId: 'add-read-img', uncheckedImage: '/images/addbooksread.png' },
        { optionId: 'readingSelect', imgId: 'add-reading-img', uncheckedImage: '/images/addreading.png' },
        { optionId: 'toReadSelect', imgId: 'add-toRead-img', uncheckedImage: '/images/addtoread.png' },
        { optionId: 'wishlistSelect', imgId: 'add-wishlist-img', uncheckedImage: '/images/wish.png' }
    ];

    // Define the checked image URL
    const checkedImage = '/images/checked.png';

    // Loop through each bookshelf configuration
    bookshelves.forEach(({ optionId, imgId, uncheckedImage }) => {
        const bookshelfId = document.getElementById(optionId)?.value;
        const imgElement = document.getElementById(imgId);

        if (bookshelfId && imgElement) {
            // Fetch the status of the book in the current bookshelf
            fetch(`/bookshelf-items/status?bookshelfId=${bookshelfId}&googleBookId=${bookID}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json(); // Parse the JSON response
                })
                .then(isInBookshelf => {
                    // Update the image source based on the book status
                    imgElement.src = isInBookshelf ? checkedImage : uncheckedImage;
                })
                .catch(error => {
                    // Handle any errors that occurred during fetch
                    console.error('Error:', error);
                });
        }
    });
}

function loadCurrentRating() {
    const bookshelfId = document.getElementById('readSelect').value; // Adjust if necessary
    const urlParams = new URLSearchParams(window.location.search);
    
    const googleBookId = urlParams.get('id');

    fetch(`/bookshelf-items/rating?bookshelfId=${bookshelfId}&googleBookId=${googleBookId}`)
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const rating = data;
        if (rating) {
            // Check the corresponding star
            document.getElementById(`starpoint_${rating * 2}`).checked = true;
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
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
    } else if (name === "Wishlist") {
        // rating a book adds it to Read
        bookshelfId = document.getElementById('wishlistSelect').value;
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
                            alert('Book rating updated successfully!\nBook exists in the Read bookshelf.');
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

function submitComment() {
    const commentText = document.getElementById('create-comment').value;
    const urlParams = new URLSearchParams(window.location.search);
    const bookId = urlParams.get('id');
    const csrf = getCsrfToken();
    
    // Get the logged-in username from the hidden input field
    const username = document.getElementById('loggedInUser').value;

    const queryString = new URLSearchParams({
        username: username,
        googleBookId: bookId,
        commentText: commentText
    }).toString();
    
    // Send the request
    fetch(`/comments/add?${queryString}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token // CSRF token for security
        },
        // Note: No body is needed since parameters are included in the URL
    })
    .then(response => {
        if (response.ok) {
            alert('Comment submitted successfully!');
            console.log(queryString);
            $('#commentModal').modal('hide');
            loadComments();
            document.getElementById('create-comment').value = '';
        } else {
            return response.text().then(text => {
                alert(`Failed to submit comment. Status: ${response.status}. Error: ${text}`);
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert(`An error occurred while submitting the comment. Details: ${error.message}`);
    });
}

function loadComments() {
    const urlParams = new URLSearchParams(window.location.search);
    var googleBookId = urlParams.get('id');

    fetch(`/comments/book/${encodeURIComponent(googleBookId)}`)
    .then(response => response.json())
    .then(comments => {
        console.log(comments); // Check the format of the response
        if (Array.isArray(comments)) {
            const commentsContainer = document.getElementById('comments-container');
            commentsContainer.innerHTML = ''; // Clear existing content

            comments.forEach(comment => {
                // console.log('Comment Properties:', Object.keys(comment));
                // console.log('Comment:', comment);
                //console.log(comment.comment.user);
                const commentCard = createCommentCard(comment);
                commentsContainer.appendChild(commentCard);
            });
        } else {
            console.error('Expected an array but received:', comments);
        }
    })
    .catch(error => console.error('Error fetching comments:', error));
}

function editComment(commentId) {
    var newCommentText = prompt("Enter new comment text:");

    if (newCommentText) {
        var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        fetch(`/comments/update/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken
            },
            body: new URLSearchParams({ newCommentText: newCommentText })
        })
        .then(response => {
            if (response.ok) {
                alert('Comment updated successfully!');
                loadComments(); // Refresh the comments section
            } else {
                alert('Failed to update comment.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while updating the comment.');
        });
    }
}

function deleteComment(commentId) {
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/comments/delete/${commentId}`, {
        method: 'DELETE',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (response.ok) {
            alert('Comment deleted successfully!');
            loadComments(); // Refresh the comments section
        } else {
            alert('Failed to delete comment.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while deleting the comment.');
    });
}

function createCommentCard(comment) {
    // Create the comment card element
    const cardDiv = document.createElement('div');
    cardDiv.classList.add('comment-card');
    let currentUserId = document.getElementById("userId");
    
    // Set the inner HTML for the card
    cardDiv.innerHTML = `
    <div class="comment-text-holder">
            <p>${comment.comment.userComment || 'No comment'}</p>
        </div>
        <div class="comment-info-holder">
            <div class="comment-profile-img">
                <img src="${comment.comment.user.profilePhoto || '/images/defaultProfile.png'}" alt="User Icon" class="user-icon comment-profile-img" id="user-icon" />
            </div>
            <div class="comment-userinfo">
                <div>${comment.comment.user.username || 'Unknown user'}</div>
                <div class="card-rate">★${comment.rating || 'N/A'}</div>
            </div>
            <div class="comment-timestamp">
                <div>Posted on: ${new Date(comment.comment.createdDate).toLocaleString()}</div>
                ${comment.comment.lastEditedDate ? 
                    `<div>Last edited on: ${new Date(comment.comment.lastEditedDate).toLocaleString()}</div>` : 
                    ''
                }
            </div>
        </div>
        ${comment.comment.user.id === currentUserId ? `
            <div class="comment-actions">
                <button onclick="editComment(${comment.comment.id})">Edit</button>
                <button onclick="deleteComment(${comment.comment.id})">Delete</button>
            </div>
        ` : ''}
    `;
    
    return cardDiv;
}
// function submitComment() {
//     // Get the book ID from the button's data attribute
//     const urlParams = new URLSearchParams(window.location.search);
//     const bookId = urlParams.get('id');
//     // Get the comment text and selected bookshelf
//     var commentText = document.querySelector('#create-comment').value;
//     // var bookshelfId = document.querySelector('#defBookshelfSelect').value;
//     bookshelfId = document.getElementById('readSelect').value;
//     const csrf = getCsrfToken();

//     // Validate input
//     if (!commentText.trim()) {
//         alert('Comment cannot be empty.');
//         return;
//     }

//     // Send the comment to the server using AJAX
//     $.ajax({
//         url: '//bookshelf-items/updateComment', 
//         method: 'POST',
//         headers: {
//             [csrf.header]: csrf.token
//         },
//         data: {
//             bookId: bookId,
//             comment: commentText,
//             bookshelfId: bookshelfId
//         },
//         success: function(response) {
//             alert('Comment added successfully!');
//             // Optionally, update the UI with the new comment
//             $('#commentModal').modal('hide');
//             // You might want to refresh the comments section here
//         },
//         error: function(xhr, status, error) {
//             alert('An error occurred while adding your comment.');
//         }
//     });
// }

