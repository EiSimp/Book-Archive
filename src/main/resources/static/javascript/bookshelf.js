// For edit popup
document.addEventListener("DOMContentLoaded", function() {
    const modal = document.getElementById("edit-bookshelf-popup");
    const closeModalBtn = document.querySelector(".close-btn");
    const saveEditBtn = document.getElementById("save-edit-btn");

    let currentBookshelfName = '';

    // Event delegation for edit buttons
    document.getElementById('library').addEventListener('click', function(event) {
        if (event.target.classList.contains('edit-btn')) {
            // Populate the modal with the current bookshelf data
            const bookshelfElement = event.target.parentElement;
            const titleElement = bookshelfElement.querySelector('.bookshelf-title');
            const isSecret = bookshelfElement.querySelector('.bookshelf-lock') !== null;

            currentBookshelfName = titleElement.innerText;

            document.getElementById("edit-bookshelf-name").value = titleElement.innerText;
            document.getElementById("edit-secret-checkbox").checked = isSecret;

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
            const libraryDiv = document.getElementById('library');
            const bookshelfElements = libraryDiv.getElementsByClassName('bookshelf');
            for (let i = 0; i < bookshelfElements.length; i++) {
                const titleElement = bookshelfElements[i].querySelector('.bookshelf-title');
                if (titleElement && titleElement.innerText === currentBookshelfName) {
                    titleElement.innerText = editedName;
                    if (isSecret) {
                        if (!bookshelfElements[i].querySelector('.bookshelf-lock')) {
                            bookshelfElements[i].insertAdjacentHTML('beforeend', '<div class="bookshelf-lock">🔒</div>');
                        }
                    } else {
                        const lockElement = bookshelfElements[i].querySelector('.bookshelf-lock');
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
});


// Open/Create Display Popup
document.getElementById("create-bookshelf-btn").onclick = function() {
    document.getElementById("create-bookshelf-popup").style.display = "flex";
};
// Close Display Popup
function closePopup() {
    document.getElementById("create-bookshelf-popup").style.display = "none";
}
// Close the popup when clicking outside of the popup content
window.onclick = function(event) {
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

        var newBookshelf = document.createElement("div");
        newBookshelf.className = "bookshelf";
        newBookshelf.innerHTML = `
            <img src="path/to/placeholder/image.png" alt="Bookshelf Image">
            <div class="bookshelf-details">
                <div class="bookshelf-title">${data.name}</div>
                ${data.secret ? '<div class="bookshelf-lock">🔒</div>' : ''}
                <button class="edit-btn">Edit</button>
            </div>
        `;
        document.getElementById("collections").appendChild(newBookshelf);
        closePopup();
    }).catch(error => {
        alert(error.message);
    });
    closePopup();
}
// For renaming and deleting bookshelves
document.addEventListener("click", function(event) {
    // Renaming
    if (event.target.classList.contains("rename-btn")) {
        var oldName = event.target.parentElement.querySelector("div").textContent;
        var newName = prompt("Enter new bookshelf name:");
        if (newName === "") {
            alert("Bookshelf name is required.");
            return;
        }
        const csrf = getCsrfToken();
        fetch('/bookshelves/rename', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrf.header]: csrf.token
            },
            body: JSON.stringify({
                oldName: oldName,
                newName: newName
            })
        }).then(response => response.json()).then(data => {
            event.target.parentElement.querySelector("div").textContent = data.name;
        }).catch(error => {
            console.error("Error renaming bookshelf:", error);
        });
    }
    // Deleting
    if (event.target.classList.contains("delete-btn")) {
        var name = event.target.parentElement.querySelector("div").textContent;
        var confirmDelete = confirm("Are you sure you want to delete this bookshelf?");
        if (confirmDelete) {
            const csrf = getCsrfToken();
            fetch('/bookshelves/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({ name: name })
            }).then(() => {
                event.target.parentElement.remove();
            }).catch(error => {
                console.error("Error deleting bookshelf:", error);
            });
        }
    }
});
// For sorting Bookshelves
document.getElementById("sort-books-btn").addEventListener("click", function() {
    var sortBy = prompt("Enter sort option (name):");
    fetch(`/bookshelves/sort?sortBy=${sortBy}`)
        .then(response => response.json())
        .then(data => {
            var container = document.getElementById("collections");
            container.innerHTML = "";
            data.forEach(function(bookshelf) {
                var bookshelfDiv = document.createElement("div");
                bookshelfDiv.className = "bookshelf";
                bookshelfDiv.innerHTML = `<div>${bookshelf.name}</div>`;
                if (bookshelf.secret) {
                    bookshelfDiv.innerHTML += `<span>🔒</span>`;
                }
                container.appendChild(bookshelfDiv);
            });
        }).catch(error => {
        console.error("Error sorting bookshelves:", error);
    });
});
// For Displaying Bookshelves
document.addEventListener("DOMContentLoaded", function() {
    fetch("/bookshelves/all") // Fetch all bookshelves from the backend
        .then(response => response.json()) // Parse the JSON response
        .then(data => {
            const libraryDiv = document.getElementById('library');
            libraryDiv.innerHTML = ''; // Clear the library div
            data.forEach(bookshelf => {
                // Create a div element for each bookshelf
                const bookshelfElement = document.createElement('div');
                bookshelfElement.className = 'bookshelf';

                // Create a div element for the bookshelf title
                const titleElement = document.createElement('div');
                titleElement.className = 'bookshelf-title';
                titleElement.innerText = bookshelf.name;

                // Create a div element for the bookshelf details
                const detailsElement = document.createElement('div');
                detailsElement.className = 'bookshelf-details';
                detailsElement.innerText = `${bookshelf.books} Books ${bookshelf.time} ago`;

                // Create the edit button
                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.innerText = 'Edit';

                // Append the title, details, and edit button to the bookshelf element
                bookshelfElement.appendChild(titleElement);
                bookshelfElement.appendChild(detailsElement);
                bookshelfElement.appendChild(editButton);

                // Append the bookshelf element to the library div
                libraryDiv.appendChild(bookshelfElement);
            });
        })
        .catch(error => console.error('Error fetching bookshelves:', error));
});
