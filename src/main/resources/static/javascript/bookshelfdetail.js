document.addEventListener("DOMContentLoaded", function () {
    const bookshelfId = document.getElementById("bookshelf-id").value;
    fetch(`/bookshelves/${bookshelfId}`)
        .then(response => response.json())
        .then(data => {
            const bookshelf = data.bookshelf;
            const items = data.items;

            // Display bookshelf info
            document.getElementById("bookshelf-name").innerText = "My Collection:  " + bookshelf.name;
            document.getElementById("bookshelf-secret").innerText = bookshelf.secret ? '🔒' : '';
            
            // Update the title
            document.title = "PagePals: " + bookshelf.name;

            const bookList = document.getElementById("book-list");
            bookList.innerHTML = ''; // Clear existing content

            items.forEach(item => {
                const book = item.book;
                const bookLi = document.createElement("li");
                bookLi.classList.add("card-li");

                const bookAnchor = document.createElement("a");
                bookAnchor.classList.add("detail-link");
                bookAnchor.href = `/bookdetail?id=${book.googleBookId}`;

                const bookCard = document.createElement("div");
                bookCard.classList.add("book-card");

                const cardThumbnailHolder = document.createElement("div");
                cardThumbnailHolder.classList.add("card-thumbnail-holder");

                const thumbnailM = document.createElement("div");
                thumbnailM.classList.add("thumbnail-m");

                const thumbnailImageM = document.createElement("div");
                thumbnailImageM.classList.add("thumbnail-image-m");

                if (item.book.thumbnailUrl) {
                    thumbnailImageM.style.backgroundImage = `url(${book.thumbnailUrl})`;
                    
                }

                thumbnailM.appendChild(thumbnailImageM);
                cardThumbnailHolder.appendChild(thumbnailM);
                bookCard.appendChild(cardThumbnailHolder);

                const cardDescription = document.createElement("div");
                cardDescription.classList.add("card-description");

                const cardDescriptionList = document.createElement("ul");
                cardDescriptionList.classList.add("card-description-li");

                const cardTitle = document.createElement("li");
                cardTitle.classList.add("card-title");
                cardTitle.innerText = book.title;

                const cardRate = document.createElement("li");
                cardRate.classList.add("card-rate");
                cardRate.innerText = item.userRating ? `Rated ★ ${item.userRating}` : `Avg ★ ${book.averageRating}`;

                cardDescriptionList.appendChild(cardTitle);
                cardDescriptionList.appendChild(cardRate);

                cardDescription.appendChild(cardDescriptionList);
                bookAnchor.appendChild(bookCard);
                bookAnchor.appendChild(cardDescription);
                bookLi.appendChild(bookAnchor);
                bookList.appendChild(bookLi);
            });

        })
        .catch(error => console.error('Error fetching bookshelf details: ', error));
})