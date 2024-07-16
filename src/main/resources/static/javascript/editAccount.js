
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

async function saveChanges(sectionID) {
    var userId = document.getElementById('userId').value;
    var formData = extractFormData(sectionID);

    if (!validateFormData(formData)) {
        return; // Stop further processing if validation fails
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const endpoint = `/user/${userId}`;

    try {
        if (sectionID === 'editUsernameSection') {
            const usernameNotAvailable = await checkUsernameIsTaken(formData.username);

            if (usernameNotAvailable) {
                displayError('usernameError', 'Error: Username is already taken');
                return;
            } else {
                hideError('usernameError');
            }
        }

        if (sectionID === 'editEmailSection') {
            const emailNotAvailable = await checkEmailIsTaken(formData.email);

            if (emailNotAvailable) {
                displayError('emailError', 'Error: Email is already taken');
                return;
            } else {
                hideError('emailError');
            }
        }

        submitFormData(formData, endpoint, csrfToken);
    } catch (error) {
        console.error('Error checking username or email availability:', error);
    }
}


function extractFormData(sectionID) {
    switch (sectionID) {
        case 'editUsernameSection':
            return {
                username: document.getElementById('newUsername').value,
                password: document.getElementById('origPassword').value,
                email: document.getElementById('origEmail').value,
                bio: document.getElementById('origBio').value
            };
        case 'editEmailSection':
            return {
                username: document.getElementById('origUsername').value,
                password: document.getElementById('origPassword').value,
                email: document.getElementById('newEmail').value,
                bio: document.getElementById('origBio').value
            };
        case 'editBioSection':
            return {
                username: document.getElementById('origUsername').value,
                password: document.getElementById('origPassword').value,
                email: document.getElementById('origEmail').value,
                bio: document.getElementById('newBio').value
            };
        default:
            console.error('Invalid sectionID');
            return null;
    }
}

function validateFormData(formData) {
    let isValid = true;

    // Validate username (only letters, numbers, and underscores)
    if ('username' in formData && !isValidUsername(formData.username)) {
        displayError('usernameError', 'Error: Username must have only letters, numbers, and underscores');
        isValid = false;
    } else {
        hideError('usernameError');
    }

    // Validate email format
    if ('email' in formData && !isValidEmail(formData.email)) {
        displayError('emailError', 'Error: Invalid email format');
        isValid = false;
    } else {
        hideError('emailError');
    }

    return isValid;
}

function displayError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.innerText = message;
    errorElement.style.display = 'block';
}

function hideError(elementId) {
    const errorElement = document.getElementById(elementId);
    errorElement.style.display = 'none';
}

function submitFormData(formData, endpoint, csrfToken) {
    const data = {
        username: formData.username,
        password: formData.password,
        email: formData.email,
        bio: formData.bio
    };

    fetch(endpoint, {
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

function isValidUsername(username) {
    // Validate username format: letters, numbers, and underscores only
    return /^\w+$/.test(username);
}

// might not be necessary since input is email type
function isValidEmail(email) {
    // Basic email format validation
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// taken from signupValidation.js
async function checkUsernameIsTaken(username) {
    try {
        const response = await fetch(`/check-username?username=${encodeURIComponent(username)}`);
        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }
        const data = await response.json();
        return data.exists;
    } catch (error) {
        console.error('Error checking username uniqueness: ', error);
        return false;
    }
}

async function checkEmailIsTaken(email) {
    try {
        const response = await fetch(`/check-email?email=${encodeURIComponent(email)}`);
        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }
        const data = await response.json();
        return data.exists;
    } catch (error) {
        console.error('Error checking email uniqueness: ', error);
        return false;
    }
}
