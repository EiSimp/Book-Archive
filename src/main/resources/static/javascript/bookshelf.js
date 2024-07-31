// For edit popup
document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("edit-bookshelf-popup");
    const closeModalBtn = document.querySelector(".close-btn");
    const saveEditBtn = document.getElementById("save-edit-btn");
    const deleteBookshelfBtn = document.getElementById("delete-bookshelf-btn");


    let currentBookshelfName = '';
    let currentBookshelfId = '';

    // Event delegation for edit buttons
    document.getElementById('collection-cardlist').addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            event.preventDefault();
            // Populate the modal with the current bookshelf data
            const bookshelfElement = event.target.parentElement;
            const titleElement = bookshelfElement.querySelector('.collection-title');
            const idElement = bookshelfElement.querySelector('.bookshelfId');
            const isSecret = bookshelfElement.querySelector('.card-lock') !== null;

            currentBookshelfName = titleElement.innerText;
            currentBookshelfId = idElement.innerText;

            document.getElementById("edit-bookshelf-name").value = titleElement.innerText;
            document.getElementById("edit-secret-checkbox").checked = isSecret;
            document.getElementById("edit-bookshelf-id").value = idElement.innerText;

            // Show the modal
            modal.style.display = "flex";
        }
    });


    // Close the modal when the user clicks on the close button
    closeModalBtn.addEventListener('click', () => {
        modal.style.display = "none";
    });

    // Close the modal when the user clicks outside of the modal content
    window.addEventListener('click', (event) => {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    });

    // Handle the save button click
    saveEditBtn.addEventListener('click', () => {
        // Save the changes
        const editedName = document.getElementById("edit-bookshelf-name").value;
        const isSecret = document.getElementById("edit-secret-checkbox").checked;

        const csrf = getCsrfToken();

        fetch('/bookshelves/rename', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrf.header]: csrf.token
            },
            body: JSON.stringify({
                oldName: currentBookshelfName,
                newName: editedName,
                secret: isSecret
            })
        }).then(response => response.json()).then(data => {
            // Update the bookshelf element with the new details
            const bookshelfUL = document.getElementById('collection-cardlist');
            const bookshelfElements = bookshelfUL.getElementsByClassName('collection-li');
            for (let i = 0; i < bookshelfElements.length; i++) {
                const titleElement = bookshelfElements[i].querySelector('.collection-title');
                if (titleElement && titleElement.innerText === currentBookshelfName) {
                    titleElement.innerText = editedName;
                    if (isSecret) {
                        if (!bookshelfElements[i].querySelector('.card-lock')) {
                            bookshelfElements[i].querySelector('.card-description-li').insertAdjacentHTML("beforeend", '<li class="card-lock">🔒</li>');
                        }
                    } else {
                        const lockElement = bookshelfElements[i].querySelector('.card-lock');
                        if (lockElement) {
                            lockElement.remove();
                        }
                    }
                    break;
                }
            }
            modal.style.display = "none";
        }).catch(error => {
            console.error("Error renaming bookshelf:", error);
            alert("Error renaming bookshelf. Please try again.");
        });
    });

    const userId = document.getElementById('userId').value;

    // Handle the delete button click
    deleteBookshelfBtn.addEventListener('click', () => {
        const confirmDelete = confirm("Are you sure you want to delete this bookshelf?");
        if (confirmDelete) {
            const csrf = getCsrfToken();
            fetch('/bookshelves/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({ 
                    id: currentBookshelfId
                 })

            }).then(() => {
                // Remove the bookshelf element from the DOM
                const bookshelfUL = document.getElementById('collection-cardlist');
                const bookshelfElements = bookshelfUL.getElementsByClassName('collection-li');
                for (let i = 0; i < bookshelfElements.length; i++) {
                    const idElement = bookshelfElements[i].querySelector('.bookshelfId');
                    if (idElement && idElement.innerText === currentBookshelfId) {
                        bookshelfElements[i].remove();
                        break;
                    }
                }
                modal.style.display = "none";
            }).catch(error => {
                console.error("Error deleting bookshelf:", error);
                alert("Error deleting bookshelf. Please try again.");
            });
        }
    });
});


// Open/Create Display Popup
document.getElementById("create-bookshelf-btn").onclick = function () {
    document.getElementById("create-bookshelf-popup").style.display = "flex";
};
// Close Display Popup
function closePopup() {
    document.getElementById("create-bookshelf-popup").style.display = "none";
}
// Close the popup when clicking outside of the popup content
window.onclick = function (event) {
    var popup = document.getElementById("create-bookshelf-popup");
    if (event.target == popup) {
        closePopup();
    }
};
//Retrieves CSRF token and header from the page
function getCsrfToken() {
    var csrfToken = document.querySelector('input[name="_csrf"]').value;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}

// Function to get the current user's ID
function getUserId() {
    return document.querySelector('meta[name="user-id"]').getAttribute('content');
}
//Creates the bookshelf
function createBookshelf() {
    var bookshelfName = document.getElementById("bookshelf-name").value;
    var isSecret = document.getElementById("secret-checkbox").checked;

    if (bookshelfName === "") {
        alert("Bookshelf name is required.");
        return;
    }

    const csrf = getCsrfToken();

    fetch('/bookshelves/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        },
        body: JSON.stringify({
            name: bookshelfName,
            secret: isSecret
        })
    }).then(response => {
        if (!response.ok) {
            throw new Error("Error creating bookshelf.");
        }
        return response.json();
    }).then(data => {
        if (!data || !data.name) {
            throw new Error("Error creating bookshelf. Please try again.");
        }

        var newBookshelf = document.createElement("li");
        newBookshelf.className = "collection-li";
        newBookshelf.innerHTML = `
            <a class="collection-detail-link">
                <div class="collection-card">
                    <div class="collection-thumbnail-holder">
                        <div class="thumbnail-s ts0">
                            <div class="thumbnail-image-s">img</div>
                        </div>
                        <div class="thumbnail-s ts1">
                            <div class="thumbnail-image-s">img</div>
                        </div>
                        <div class="thumbnail-s ts2">
                            <div class="thumbnail-image-s">img</div>
                        </div>
                        <div class="thumbnail-s ts3">
                            <div class="thumbnail-image-s">img</div>
                        </div>
                        <div class="thumbnail-s ts4">
                            <div class="thumbnail-image-s">img</div>
                        </div>
                    </div>
                </div>
                <div class="card-description">
                    <ul class="card-description-li">
                        <li class="collection-title">${data.name}</li>
                        <li class="card-numOfBooks">${data.books} Books</li>
                        ${data.secret ? '<li class="card-lock">🔒</li>' : ""}
                        <li class="booshelfId" style="display:none;">${data.id}</li>
                    </ul>
                    
                    <button class="edit-btn">Edit</button>
                </div>
             </a>
        `;
        document.getElementById("collection-cardlist").appendChild(newBookshelf);
        closePopup();
    }).catch(error => {
        alert(error.message);
    });
    closePopup();
}
// For sorting Bookshelves
document.getElementById("sort-books-btn").addEventListener("click", function () {
    var sortBy = prompt("Enter sort option (name):");
    fetch(`/bookshelves/sort?sortBy=${sortBy}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok '+ response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            var container = document.getElementById("collection-cardlist");
            container.innerHTML = "";
            data.forEach(bookshelf => {
                var bookshelfLi = document.createElement("li");
                bookshelfLi.className = "collection-li";
                bookshelfLi.innerHTML =
                    `<a class="collection-detail-link">
                        <div class="collection-card">
                            <div class="collection-thumbnail-holder">
                                <div class="thumbnail-s ts0">
                                    <div class="thumbnail-image-s" id="thumbnail-0-${bookshelf.id}"></div>
                                </div>
                                <div class="thumbnail-s ts1">
                                    <div class="thumbnail-image-s" id="thumbnail-1-${bookshelf.id}"></div>
                                </div>
                                <div class="thumbnail-s ts2">
                                    <div class="thumbnail-image-s" id="thumbnail-2-${bookshelf.id}"></div>
                                </div>
                                <div class="thumbnail-s ts3">
                                    <div class="thumbnail-image-s" id="thumbnail-3-${bookshelf.id}"></div>
                                </div>
                                <div class="thumbnail-s ts4">
                                    <div class="thumbnail-image-s" id="thumbnail-4-${bookshelf.id}"></div>
                                </div>
                            </div>
                        </div>
                        <div class="card-description">
                            <ul class="card-description-li">
                                <li class="collection-title">${bookshelf.name}</li>
                                <li class="card-numOfBooks">${bookshelf.books} Books</li>
                                ${bookshelf.secret ? '<li class="card-lock">🔒</li>' : ""}
                                <li class="booshelfId" style="display:none;">${bookshelf.id}</li>
                            </ul>
                            <button class="edit-btn">Edit</button>
                        </div>
                    </a>`;
                container.appendChild(bookshelfLi);

                fetchAndSetThumbnails(bookshelf.id);
            });
        }).catch(error => {
            console.error("Error sorting bookshelves:", error);
        });
});

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

// For Displaying Bookshelves
document.addEventListener("DOMContentLoaded", function () {
    const excludedBookshelves = ['Read', 'Reading', 'To Read'];
    fetch("/bookshelves/all") // Fetch all bookshelves from the backend
        .then(response => response.json()) // Parse the JSON response
        .then(data => {
            const bookshelfUL = document.getElementById('collection-cardlist');
            bookshelfUL.innerHTML = ''; // Clear the library div
            data.forEach(bookshelf => {
                if (excludedBookshelves.includes(bookshelf.name)) {
                    return; // Skip this bookshelf
                }
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
                if (data.secret) {
                    const lockElement = document.createElement("li");
                    lockElement.className="card-lock";
                    lockElement.innerText = "🔒";
                    cardDescriptionList.appendChild(lockElement);
                }

                // Create the edit button
                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.innerText = 'Edit';

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

                cardDescription.appendChild(editButton);

                // Append the anchor element to the list item
                bookshelfElement.appendChild(anchorElement);

                // Append the bookshelf element to the bookshelf ul
                bookshelfUL.appendChild(bookshelfElement);

                fetchAndSetThumbnails(bookshelf.id);
            });
        })
        .catch(error => console.error('Error fetching bookshelves:', error));
});


