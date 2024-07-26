document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("create-bookclub-popup");
    const closeModalBtn = document.querySelector(".close-btn");
    const saveBtn = document.getElementById("save-btn");
    
    // Event delegation for edit buttons
    document.getElementById('bookclub-create').addEventListener('click', function (event) {
            modal.style.display = "flex";
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
})