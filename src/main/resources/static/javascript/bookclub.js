document.addEventListener("DOMContentLoaded", function () {
    var createButton = document.getElementById("bookclub-create");
    var joinButton = document.getElementById("bookclub-join");
    var modal = document.getElementById("create-bookclub-popup");
    var closeButton = document.querySelector(".close-btn");
    var saveButton = document.getElementById("save-btn");
    window.existingClubsDisplayed = false;

    createButton.onclick = function () {
        modal.style.display = "flex";
    };

    joinButton.onclick = function () {
        window.existingClubsDisplayed = true;
        loadExistingClubs();
    };

    closeButton.addEventListener('click', () => {
        modal.style.display = "none";
    });

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

    saveButton.onclick = function () {
        var name = document.getElementById("create-bookclub-name").value;
        var description = document.getElementById("create-bookclub-description").value;

        if (name && description) {
            const csrf = getCsrfToken();

            if (!csrf) {
                console.error("CSRF token not found");
                alert("CSRF token not found. Please refresh the page.");
                return;
            }

            fetch('/bookclubs/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({ name: name, description: description })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Failed to create book club');
                    }
                })
                .then(data => {
                    alert('Book club created successfully');
                    modal.style.display = "none";
                    loadMyClubs();
                    if (window.existingClubsDisplayed) {
                        loadExistingClubs();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error creating book club');
                });
        } else {
            alert("Please fill in all fields.");
        }
    };

    loadMyClubs();
});

function getCsrfToken() {
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}

function loadMyClubs() {
    const userId = getCurrentUserId();

    if (!userId) {
        console.error('User ID not found');
        return;
    }

    fetch(`/bookclubmembers/user/${userId}/bookclubs`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to load book clubs');
            }
        })
        .then(data => {
            updateBookClubsUI(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateBookClubsUI(bookClubs) {
    const tableSection = document.querySelector('.bookclub-table-section');
    const tableBody = document.querySelector('.bookclub-table-section tbody');

    if (bookClubs.length === 0) {
        tableSection.style.display = 'none';
    } else {
        tableSection.style.display = 'flex';
        tableBody.innerHTML = '';

        bookClubs.forEach(bookClub => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="table-name"><a href="/bookclubs/details/view/${bookClub.id}">${bookClub.name}</a></td>
                <td class="table-count"><span id="members-count-${bookClub.id}">Loading...</span></td>
                <td class="table-button"><button onclick="leaveBookClub(${bookClub.id}, ${bookClub.manager.id})">Leave</button></td>
            `;
            tableBody.appendChild(row);

            loadMembersCount(bookClub.id);
        });
    }
}

function loadMembersCount(bookClubId) {
    fetch(`/bookclubmembers/members/${bookClubId}`)
        .then(response => response.json())
        .then(data => {
            const membersCount = data.length;
            document.getElementById(`members-count-${bookClubId}`).innerText = `(${membersCount}/15)`;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById(`members-count-${bookClubId}`).innerText = 'Error';
        });
}

function leaveBookClub(bookClubId, managerUserId) {
    const userId = getCurrentUserId();

    if (!userId) {
        console.error('User ID not found');
        return;
    }

    if (userId == managerUserId) {
        alert('You are the manager of this book club and cannot leave. Please transfer the manager role before leaving.');
        return;
    }

    const csrf = getCsrfToken();

    fetch('/bookclubmembers/removeOrLeave', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        },
        body: JSON.stringify({ bookClubId: bookClubId, userId: userId, managerUserId: managerUserId })
    })
        .then(response => {
            if (response.ok) {
                alert('Left the book club successfully');
                loadMyClubs();
                if (window.existingClubsDisplayed) {
                    loadExistingClubs();
                }
            } else {
                throw new Error('Failed to leave the book club');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error leaving the book club');
        });
}

function loadExistingClubs() {
    fetch('/bookclubs/all')
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to load existing clubs');
            }
        })
        .then(data => {
            updateExistingClubsUI(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateExistingClubsUI(clubs) {
    const existingClubsSection = document.getElementById('existing-clubs-section');
    const existingClubsTbody = document.getElementById('existing-clubs-tbody');

    if (clubs.length === 0) {
        existingClubsSection.style.display = 'none';
    } else {
        existingClubsSection.style.display = 'flex';
        existingClubsTbody.innerHTML = '';

        clubs.forEach(club => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="table-name"><a href="/bookclubs/details/view/${club.id}">${club.name}</a></td>
                <td class="table-count"><span id="members-count-${club.id}">Already a member</span></td>
                <td class="table-button"><button onclick="joinBookClub(${club.id})">Join</button></td>
            `;
            existingClubsTbody.appendChild(row);

            loadMembersCount(club.id);
        });
    }
}

function joinBookClub(bookClubId) {
    const userId = getCurrentUserId();
    const csrf = getCsrfToken();

    fetch(`/bookclubmembers/user/${userId}/bookclubs`)
        .then(response => response.json())
        .then(data => {
            const isMember = data.some(club => club.id === bookClubId);
            if (isMember) {
                alert('You are already a member of this book club');
            } else {
                fetch('/bookclubmembers/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrf.header]: csrf.token
                    },
                    body: JSON.stringify({ bookClubId: bookClubId, userId: userId })
                })
                    .then(response => {
                        if (response.ok) {
                            alert('Joined the book club successfully');
                            loadMyClubs();
                            if (window.existingClubsDisplayed) {
                                loadExistingClubs();
                            }
                        } else {
                            throw new Error('Failed to join the book club');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Error joining the book club');
                    });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error checking membership status');
        });
}

function getCurrentUserId() {
    return document.getElementById('userId').value;
}
