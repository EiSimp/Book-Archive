// Function to switch to edit mode
function editField() {
    // Get current display values and populate edit form
    var username = document.getElementById("usernameDisplay").innerText;
    var password = document.getElementById("passwordDisplay").innerText;
    var bio = document.getElementById("bioDisplay").innerText;

    document.getElementById("editUsername").value = username;
    document.getElementById("editPassword").value = password;

    if (bio == "") {
        // If the bio section is empty
        document.getElementById("editBio").value = ""; // Set the text box value to empty
    } else {
        // If the bio section contains actual bio content
        document.getElementById("editBio").value = bio; // Set the text box value to the bio content
    }

    // Hide user display, show edit form
    document.getElementById("userDisplay").style.display = "none";
    document.getElementById("manageAccountDisplay").style.display = "none";
    document.getElementById("editForm").style.display = "block";
}

// Function to cancel edit mode
function cancelEdit() {
    // Hide edit form, show user display
    document.getElementById("editForm").style.display = "none";
    document.getElementById("userDisplay").style.display = "block";
    document.getElementById("manageAccountDisplay").style.display = "block";
}

function deleteUser() {

    const userId = document.getElementById('userId').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    fetch(`/user/${userId}`, {
        method: 'DELETE',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete user');
        }
        console.log('User deleted successfully');
        // Redirect to login page
        // TODO: can remove this line if forced redir. is done later
        window.location.href = '/login';
    })
    .catch(error => {
        console.error('Error deleting user:', error);
        // Handle error (e.g., show error message)
    });
}

function saveChanges() {
    const userId = document.getElementById('userId').value;
    const username = document.getElementById('editUsername').value;
    const password = document.getElementById('editPassword').value;
    const bio = document.getElementById('editBio').value;

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    const data = {
        username: username,
        password: password,
        bio: bio
    };

    fetch(`/user/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        console.log('User updated successfully');
        window.location.reload(); // reload the page
    })
    .catch(error => {
        console.error('Error updating user:', error);
    });
}
