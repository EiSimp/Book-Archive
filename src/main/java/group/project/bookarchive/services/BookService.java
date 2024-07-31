package group.project.bookarchive.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.repositories.BookRepository;

@Service
public class BookService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Value("${google.api.key}")
    private String apiKey;

    public Book fetchBookDetails(String googleBookId) throws IOException {
        // Check if the book exists in the database
        Book book = bookRepository.findByGoogleBookId(googleBookId);
        if (book != null) {
            return book; // Return the book from the database
        }

        // If the book doesn't exist in the database, fetch from the Google Books API
        return fetchBookDetailsFromAPI(googleBookId);
    }

    public Book fetchBookDetailsFromAPI(String googleBookId) throws IOException {
        String url = "https://www.googleapis.com/books/v1/volumes/" + googleBookId + "?key=" + apiKey;
        String jsonResponse = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(jsonResponse);

        Book book = new Book();
        JsonNode volumeInfo = root.path("volumeInfo");

        book.setGoogleBookId(googleBookId);
        book.setTitle(volumeInfo.path("title").asText(""));
        book.setAuthor(getFirstAuthor(volumeInfo.path("authors")));
        book.setPublisher(volumeInfo.path("publisher").asText(""));
        book.setPublishedDate(volumeInfo.path("publishedDate").asText(""));
        book.setDescription(volumeInfo.path("description").asText(""));
        book.setCategory(getFirstCategory(volumeInfo.path("categories")));
        book.setThumbnailUrl(volumeInfo.path("imageLinks").path("thumbnail").asText(""));
        // book.setBiggerThumbnailUrl(getLargerImageUrl(volumeInfo.path("imageLinks")));
        book.setBiggerThumbnailUrl(getLargerImgUrl(googleBookId));
        book.setAverageRating(volumeInfo.path("averageRating").asDouble(0.0));
        book.setIsbn(getFirstIsbn(volumeInfo.path("industryIdentifiers")));
        book.setAuthorDescription(""); // Adjust as needed

        return book;
    }

    private String getFirstAuthor(JsonNode authorsNode) {
        if (authorsNode != null && authorsNode.isArray() && authorsNode.size() > 0) {
            return authorsNode.get(0).asText("");
        }
        return "";
    }

    private String getFirstCategory(JsonNode categoriesNode) {
        if (categoriesNode != null && categoriesNode.isArray() && categoriesNode.size() > 0) {
            return categoriesNode.get(0).asText("");
        }
        return "";
    }

    private String getFirstIsbn(JsonNode industryIdentifiersNode) {
        if (industryIdentifiersNode != null && industryIdentifiersNode.isArray()
                && industryIdentifiersNode.size() > 0) {
            return industryIdentifiersNode.get(0).path("identifier").asText("");
        }
        return "";
    }

    private String getLargerImageUrl(JsonNode imageLinksNode) {
        if (imageLinksNode != null) {
            if (imageLinksNode.has("extraLarge")) {
                return imageLinksNode.path("extraLarge").asText("");
            } else if (imageLinksNode.has("large")) {
                return imageLinksNode.path("large").asText("");
            } else if (imageLinksNode.has("medium")) {
                return imageLinksNode.path("medium").asText("");
            }
        }
        return imageLinksNode.path("thumbnail").asText(""); // Fallback to thumbnail if no larger image is available
    }

    private String getLargerImgUrl(String id) {
        String url = "https://books.google.com/books/content?id=" + id
                + "&printsec=frontcover&img=1&zoom=0&source=gbs_api";
        return url;
    }
}
