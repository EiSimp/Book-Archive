function updateProfileImage(selectElement) {
    var selectedValue = selectElement.value;
    if (selectedValue.startsWith('/images/')) {
        // If it's a predefined option, update immediately
        document.getElementById('profile-user-icon').src = selectedValue;
    } else {
        // If it's a custom file, show a preview
        var file = selectElement.files[0];
        var reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('profile-user-icon').src = e.target.result;
            uploadProfilePhoto(file);
        };

        reader.readAsDataURL(file);
        //document.getElementById('selectedPhoto').value = ''; // Clear hidden field for custom file
    }
}
