document.addEventListener("DOMContentLoaded", function () {
    const userId = document.getElementById("userId").value;
    const managerId = document.getElementById("managerId").value;
    const bookClubId = document.getElementById("bookClubId").value;
    const csrf = getCsrfToken();


    window.openUpdateEventModal = function (eventId, title, description, dateTime) {
        document.getElementById('updateEventId').value = eventId;
        document.getElementById('updateEventTitle').value = title;
        document.getElementById('updateEventDescription').value = description;
        document.getElementById('updateEventDateTime').value = dateTime.replace(" ", "T");
        document.getElementById('updateEventModal').style.display = 'block';
    }


    window.closeUpdateEventModal = function () {
        document.getElementById('updateEventModal').style.display = 'none';
    }

    window.openDeleteEventModal = function (eventId) {
        document.getElementById('deleteEventId').value = eventId;
        document.getElementById('deleteEventModal').style.display = 'block';
    }

    window.closeDeleteEventModal = function () {
        document.getElementById('deleteEventModal').style.display = 'none';
    }

    window.submitUpdateEventForm = function () {
        const eventId = document.getElementById('updateEventId').value;
        const eventTitle = document.getElementById('updateEventTitle').value;
        const eventDescription = document.getElementById('updateEventDescription').value;
        const eventDateTime = document.getElementById('updateEventDateTime').value;

        if (eventTitle && eventDescription && eventDateTime) {
            fetch(`/api/events/update/${eventId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    title: eventTitle,
                    description: eventDescription,
                    dateTime: eventDateTime,
                    bookClub: {
                        id: bookClubId
                    }
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Event updated successfully.");
                        location.reload();
                    } else {
                        alert("Failed to update event.");
                    }
                })
                .catch(error => console.error("Error updating event:", error));
        }
    }

    window.submitDeleteEventForm = function () {
        const eventId = document.getElementById('deleteEventId').value;

        fetch(`/api/events/delete/${eventId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                [csrf.header]: csrf.token
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("Event deleted successfully.");
                    location.reload();
                } else {
                    alert("Failed to delete event.");
                }
            })
            .catch(error => console.error("Error deleting event:", error));
    }

    window.openTransferRoleModal = function () {
        fetch(`/bookclubmembers/members/${bookClubId}`)
            .then(response => response.json())
            .then(members => {
                const select = document.getElementById("newManagerSelect");
                select.innerHTML = "";
                members.forEach(member => {
                    if (member.user.id !== userId) {
                        const option = document.createElement("option");
                        option.value = member.user.id;
                        option.text = member.user.username;
                        select.add(option);
                    }
                });
                document.getElementById("transferRoleModal").style.display = "block";
            })
            .catch(error => console.error("Error fetching members:", error));
    }

    window.closeTransferRoleModal = function () {
        document.getElementById("transferRoleModal").style.display = "none";
    }

    window.transferRole = function () {
        const newManagerUserId = document.getElementById("newManagerSelect").value;
        if (newManagerUserId === userId) {
            alert("You cannot transfer the role to yourself.");
            return;
        }
        if (newManagerUserId) {
            fetch(`/bookclubs/transfer`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    bookClubId: bookClubId,
                    newManagerUserId: newManagerUserId,
                    currentManagerUserId: userId
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Role transferred successfully.");
                        location.reload();
                    } else {
                        alert("Failed to transfer role.");
                    }
                })
                .catch(error => console.error("Error transferring role:", error));
        }
    }

    window.openCreateEventModal = function () {
        document.getElementById('createEventModal').style.display = 'block';
    }

    window.closeCreateEventModal = function () {
        document.getElementById('createEventModal').style.display = 'none';
    }

    window.submitCreateEventForm = function () {
        const eventTitle = document.getElementById('createEventTitle').value;
        const eventDescription = document.getElementById('createEventDescription').value;
        const eventDateTime = document.getElementById('createEventDateTime').value;

        if (eventTitle && eventDescription && eventDateTime) {
            fetch(`/api/events/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    title: eventTitle,
                    description: eventDescription,
                    dateTime: eventDateTime,
                    bookClub: {
                        id: bookClubId
                    }
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Event created successfully.");
                        location.reload();
                    } else {
                        alert("Failed to create event.");
                    }
                })
                .catch(error => console.error("Error creating event:", error));
        } else {
            alert("All fields are required.");
        }
    }


    window.renameBookClub = function () {
        const newName = prompt("Enter the new name for the book club:");
        if (newName) {
            fetch(`/bookclubs/rename`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    id: bookClubId,
                    newName: newName,
                    userId: userId
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Book club renamed successfully.");
                        location.reload();
                    } else {
                        alert("Failed to rename book club.");
                    }
                })
                .catch(error => console.error("Error renaming book club:", error));
        }
    }

    window.deleteBookClub = function () {
        if (confirm("Are you sure you want to delete this book club?")) {
            fetch(`/bookclubs/delete`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    id: bookClubId,
                    userId: userId
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Book club deleted successfully.");
                        window.location.href = "/bookclubs";
                    } else {
                        alert("Failed to delete book club.");
                    }
                })
                .catch(error => console.error("Error deleting book club:", error));
        }
    }

    window.openRemoveMemberModal = function () {
        fetch(`/bookclubmembers/members/${bookClubId}`)
            .then(response => response.json())
            .then(members => {
                const select = document.getElementById("removeMemberSelect");
                select.innerHTML = "";
                members.forEach(member => {
                    if (member.user.id !== userId) {
                        const option = document.createElement("option");
                        option.value = member.user.id;
                        option.text = member.user.username;
                        select.add(option);
                    }
                });
                document.getElementById("removeMemberModal").style.display = "block";
            })
            .catch(error => console.error("Error fetching members:", error));
    }

    window.closeRemoveMemberModal = function () {
        document.getElementById("removeMemberModal").style.display = "none";
    }

    window.removeMember = function () {
        const memberId = document.getElementById("removeMemberSelect").value;
        if (memberId) {
            if (memberId === userId) {
                alert("You cannot remove yourself as you are the manager.");
                return;
            }
            fetch(`/bookclubmembers/removeOrLeave`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.header]: csrf.token
                },
                body: JSON.stringify({
                    bookClubId: bookClubId,
                    userId: memberId,
                    managerUserId: userId
                }),
            })
                .then(response => {
                    if (response.ok) {
                        alert("Member removed successfully.");
                        location.reload();
                    } else {
                        alert("Failed to remove member.");
                    }
                })
                .catch(error => console.error("Error removing member:", error));
        }
    }

    // Event listeners for dropdown actions
    const settingsButton = document.querySelector(".dropdown button");
    settingsButton.addEventListener("click", function (event) {
        event.stopPropagation();
        const dropdownContent = this.nextElementSibling;
        dropdownContent.classList.toggle("show");
    });

    document.addEventListener("click", function (event) {
        const isClickInsideDropdown = event.target.closest(".dropdown");
        if (!isClickInsideDropdown) {
            const dropdowns = document.querySelectorAll(".dropdown-content");
            dropdowns.forEach(dropdown => {
                dropdown.classList.remove("show");
            });
        }
    });
});

function getCsrfToken() {
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return {
        token: csrfToken,
        header: csrfHeader
    };
}
