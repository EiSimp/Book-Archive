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

function saveChanges() {
    const userId = document.getElementById('userId').value;
    const username = document.getElementById('editUsername').value;
    const password = document.getElementById('editPassword').value;
    const bio = document.getElementById('editBio').value;

    // requestParam making passing in values long :D
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
        window.location.reload(); // Example to reload the page
    })
    .catch(error => {
        console.error('Error updating user:', error);
        // Handle error appropriately
    });
}
