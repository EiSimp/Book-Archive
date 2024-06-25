// Function to switch to edit mode
function editField() {
    // Get current display values and populate edit form
    var username = document.getElementById("usernameDisplay").innerText;
    var birthday = document.getElementById("birthdayDisplay").innerText;
    var bio = document.getElementById("bioDisplay").innerText;

    document.getElementById("editUsername").value = username;
    document.getElementById("editBirthday").value = birthday;
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

// Handle form submission (save changes)
document.getElementById("editForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent default form submission

    // Get updated values from edit form
    var updatedUsername = document.getElementById("editUsername").value;
    var updatedBirthday = document.getElementById("editBirthday").value;
    var updatedBio = document.getElementById("editBio").value;

    // Update display with new values
    document.getElementById("usernameDisplay").innerText = updatedUsername;
    document.getElementById("birthdayDisplay").innerText = updatedBirthday;
    document.getElementById("bioDisplay").innerText = updatedBio;

    // Exit edit mode
    cancelEdit();
});
