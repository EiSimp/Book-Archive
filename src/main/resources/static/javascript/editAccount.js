
function showEdit(sectionId) {
    switch (sectionId) {
        case 'editUsernameSection':
            var username = document.getElementById('usernameDisplay').innerText;
            document.getElementById('newUsername').value = username;
            break;
        
        case 'editPasswordSection':
            // Clear previous inputs for security reasons or handle accordingly
            document.getElementById('newPassword').value = '';
            break;

        case 'editEmailSection':
            var email = document.getElementById("emailDisplay").innerText;
            document.getElementById("newEmail").value = email;
            break;
        
        case 'editBioSection':
            var bio = document.getElementById("bioDisplay").innerText;
            if (bio == "") {
                // If the bio section is empty
                document.getElementById("newBio").value = ""; // Set the text box value to empty
            } else {
                // If the bio section contains actual bio content
                document.getElementById("newBio").value = bio; // Set the text box value to the bio content
            }
            break;
        
        default:
            break;
    }

    document.getElementById(sectionId).style.display = 'block';
    // Hide user display, show edit form
    document.getElementById("userDisplay").style.display = "none";
    document.getElementById("manageAccountDisplay").style.display = "none";
}

function cancelEdit(sectionId) {
    document.getElementById(sectionId).style.display = 'none';
    document.getElementById("userDisplay").style.display = "flex";
    document.getElementById("manageAccountDisplay").style.display = "flex";
}

function deleteUser() {
    if (!confirm('Are you sure you want to delete your account?')) {
        return; // Cancel deletion if user clicks Cancel in the dialog
    }

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
            // redirect to login
            window.location.href = '/login';
        })
        .catch(error => {
            console.error('Error deleting user:', error);

        });
}

function saveChanges(sectionID) {
    var userId = document.getElementById('userId').value;

    var username;
    var password;
    var email;
    var bio;

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    switch (sectionID) {
        case 'editUsernameSection':
            username = document.getElementById('newUsername').value;
            password = document.getElementById('origPassword').value;
            email = document.getElementById('origEmail').value;
            bio = document.getElementById('origBio').value;
            break;
        case 'editEmailSection':
            username = document.getElementById('origUsername').value;
            password = document.getElementById('origPassword').value;
            email = document.getElementById('newEmail').value;
            bio = document.getElementById('origBio').value;
            break;
        case 'editBioSection':
            username = document.getElementById('origUsername').value;
            password = document.getElementById('origPassword').value;
            email = document.getElementById('origEmail').value;
            bio = document.getElementById('newBio').value;
            break;
        default:
            console.error('Invalid sectionID');
            return;
    }

    const data = {
        username: username,
        password: password,
        email: email,
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
