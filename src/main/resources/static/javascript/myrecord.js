document.addEventListener("DOMContentLoaded", function () {
    getData();

});


function getData() {
    fetch("/bookshelves/all")
        .then(response => response.json())
        .then(data =>{
            const totalCount = data.length;
            const readShelf = data.find(bookshelf => bookshelf.name === "Read");
            if (readShelf) {
                fetch(`/bookshelves/${readShelf.id}`).then(response => response.json()).then(items => {
                    processAndDisplayData(items, totalCount);
                })
                .catch(error => console.error('Error fetching data: ', error));
            } else {
                console.error('Read bookshelf not found');
            }
        })
        .catch(error => console.error('Error fetching data: ',error));
}

function processAndDisplayData(items, collectionsMade) {
    const stats = {
        booksRead: 0,
        booksRated: 0,
        collectionsMade: collectionsMade,
        commentsLeft: 0,
        totalRating: 0,
        ratingDistribution: Array(10).fill(0), // Rating from 0.5 to 5.0 in 0.5 increments
        authorScores: {},
        categoryScores: {}
    };

    //Compute statistics
    items.forEach(item => {
        stats.booksRead++;
        if (item.userRating > 0) {
            stats.booksRated++;
            stats.totalRating += item.userRating;
            const ratingIndex = Math.round(item.userRating * 2) - 1; // Convert rating to index
            stats.ratingDistribution[ratingIndex]++;
        }
        if (item.userComment) {
            stats.commentsLeft++;
        }
        // Calculate author scores
        const author = item.book.author;
        if (author) {
            if (!stats.authorScores[author]) {
                stats.authorScores[author] = 0;
            }
            stats.authorScores[author]++;
        }

        // Calculate category scores
        const categories = item.book.category ? item.book.category.split(" / ") : []; // Split categories by " / "
        categories.forEach(category => {
            if (!stats.categoryScores[category]) {
                stats.categoryScores[category] = 0;
            }
            stats.categoryScores[category]++;
        });
    });

    const averageRating = stats.booksRated > 0 ? (stats.totalRating / stats.booksRated).toFixed(1) : 0;


    // Find the most frequent rating
    const mostFrequentRatingIndex = stats.ratingDistribution.indexOf(Math.max(...stats.ratingDistribution));
    const mostFrequentRating = (mostFrequentRatingIndex + 1) / 2;

    // Display statistics
    document.getElementById("number-of-books-read").innerText = stats.booksRead;
    document.getElementById("number-of-books-rated").innerText = stats.booksRated;
    document.getElementById("number-of-rating").innerText = stats.booksRated;
    document.getElementById("number-of-collections-made").innerText = stats.collectionsMade;
    document.getElementById("number-of-comments-left").innerText = stats.commentsLeft;
    document.getElementById("average-rating").innerText = averageRating;
    document.getElementById("most-frequent-rating").innerText = mostFrequentRating.toFixed(1);
    
    // Display rating distribution
    const ratingLabels = ['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0'];
    const ratingDistribution = document.querySelector(".record-rating-graph");
    ratingDistribution.innerHTML = '';
    stats.ratingDistribution.forEach((count, index) => {
        const barWrap = document.createElement("div");
        barWrap.className = "record-rating-graphbar-wrap";

        const bar = document.createElement("span");
        bar.className = "record-rating-graph-bar";
        bar.style.height = `${count * 10}px`;
        if (index === mostFrequentRatingIndex) {
            bar.style.backgroundColor = "#fd6058"; // Highlight the most frequent rating
        }


        const label = document.createElement("span");
        label.className = "record-rating-graph-label";
        label.innerText = ratingLabels[index];

        barWrap.appendChild(bar);
        barWrap.appendChild(label);
        ratingDistribution.appendChild(barWrap);
    });

    // Display favorite authors ranking
    const favoriteAuthors = Object.entries(stats.authorScores)
        .sort((a, b) => b[1] - a[1]) // Sort authors by score in descending order
        .slice(0, 10); // Take the top 10 authors

    const favoriteAuthorsList = document.querySelector(".record-favorite-author-rank-ul");
    favoriteAuthorsList.innerHTML = '';
    favoriteAuthors.forEach((author, index) => {
        const li = document.createElement("li");
        li.className = "record-favorite-author-rank-li";
        li.innerHTML = `
            <div class="author-rank-li-wrapper">
                <div class="record-favorite-author-rank-num">
                    <div class="ranknum">${index + 1}</div>
                </div>
                <div class="record-favorite-author-rank-name">
                    <div class="rankname">${author[0]}</div>
                    <span class="record-favorite-author-score">Scored ${author[1]}</span>
                </div>
            </div>`;
        favoriteAuthorsList.appendChild(li);
    });

    // Display favorite categories
    const favoriteCategories = Object.entries(stats.categoryScores)
        .sort((a, b) => b[1] - a[1]) // Sort categories by score in descending order
        .slice(0, 10); // Take the top 10 categories

    const tagStyles = [
        { size: "35px", top: "30%", left: "20%" },
        { size: "30px", top: "20%", left: "45%" },
        { size: "25px", top: "50%", left: "70%" },
        { size: "14px", top: "10%", left: "80%" },
        { size: "14px", top: "60%", left: "30%" },
        { size: "12px", top: "40%", left: "60%" },
        { size: "12px", top: "70%", left: "40%" },
        { size: "12px", top: "80%", left: "20%" },
        { size: "12px", top: "80%", left: "50%" },
        { size: "12px", top: "30%", left: "90%" }
    ];

    const tagCloud = document.getElementById("tag-cloud");
    tagCloud.innerHTML = '';
    favoriteCategories.forEach((category, index) => {
        const tag = tagStyles[index] || tagStyles[tagStyles.length - 1]; // Use last style as fallback
        const tagElement = document.createElement("span");
        tagElement.className = "tag";
        tagElement.style.fontSize = tag.size;
        tagElement.style.top = tag.top;
        tagElement.style.left = tag.left;
        tagElement.textContent = category[0];
        tagElement.style.backgroundColor = "white";
        tagElement.style.border = "3px solid #ddd";
        tagElement.style.borderRadius = "10px";
        tagElement.style.padding = "0 10px";
        tagCloud.appendChild(tagElement);
    });
}

function tagCloud() {
    const tags = [
        { text: "Literature", size: "38px", top: "30%", left: "20%" },
        { text: "Sci-Fi", size: "30px", top: "15%", left: "50%" },
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
        tagElement.style.backgroundColor = "white";
        tagElement.style.border = "3px solid #ddd";
        tagElement.style.borderRadius = "10px";
        tagElement.style.padding = "0 10px";
        tagCloud.appendChild(tagElement);
    });
}
