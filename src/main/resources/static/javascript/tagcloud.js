document.addEventListener("DOMContentLoaded", function () {
    const tags = [
        { text: "Literature", size: "35px", top: "30%", left: "20%" },
        { text: "Sci-Fi", size: "30px", top: "20%", left: "50%" },
        { text: "Drama", size: "25px", top: "50%", left: "70%" },
        { text: "Classic", size: "16px", top: "10%", left: "80%" },
        { text: "Culture", size: "16px", top: "60%", left: "30%" },
        { text: "LiberalArts", size: "16px", top: "40%", left: "60%" },
        { text: "Painting", size: "16px", top: "70%", left: "40%" },
        { text: "Philosophy", size: "16px", top: "80%", left: "20%" },
        { text: "Art", size: "16px", top: "80%", left: "50%" },
        { text: "Fiction", size: "16px", top: "30%", left: "90%" }
    ];

    const tagCloud = document.getElementById("tag-cloud");

    tags.forEach(tag => {
        const tagElement = document.createElement("span");
        tagElement.className = "tag";
        tagElement.style.fontSize = tag.size;
        tagElement.style.top = tag.top;
        tagElement.style.left = tag.left;
        tagElement.textContent = tag.text;
        tagCloud.appendChild(tagElement);
    });
});
