// Function to switch to edit mode
// birthday fields are commented out, as dates are not implemented currently
// Function to switch to edit mode
function editField() {
    // Get current display values and populate edit form
    var username = document.getElementById("usernameDisplay").innerText;
    var password = document.getElementById("passwordDisplay").innerText;
    //var birthday = document.getElementById("birthdayDisplay").innerText;
    var bio = document.getElementById("bioDisplay").innerText;

    document.getElementById("editUsername").value = username;
    document.getElementById("editPassword").value = password;
    //document.getElementById("editBirthday").value = birthday;
    document.getElementById("editBio").value = bio;

    // Hide user display, show edit form
    document.getElementById("userDisplay").style.display = "none";
    document.getElementById("editForm").style.display = "block";
}

// Function to cancel edit mode
function cancelEdit() {
    // Hide edit form, show user display
    document.getElementById("editForm").style.display = "none";
    document.getElementById("userDisplay").style.display = "block";
}

function deleteUser() {
    const userId = document.getElementById('userId').value;
    fetch(`/user/${userId}`, {
        method: 'DELETE',
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

    // requestParam making passing in values long :D
    // update later with better fetch?
    fetch(`/user/${userId}?username=${username}&password=${password}&bio=${bio}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
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
    // Put extra error handlers here
});

}
