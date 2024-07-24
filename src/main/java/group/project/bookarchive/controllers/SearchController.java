package group.project.bookarchive.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import group.project.bookarchive.models.BookDTO;
import group.project.bookarchive.utils.PaginationUtils;
import group.project.bookarchive.utils.PaginationUtils.PaginationInfo;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Value("${google.api.key}")
    private String apiKey;

    @GetMapping
    public String getSearchResult(@RequestParam("q") Optional<String> q, @RequestParam("page") Optional<Integer> page,
            Model model) {
        int currentPage = page.orElse(1);
        int booksPerPage = 18;
        int pageGroupSize = 10;
        // Check if the search query is empty
        if (!q.isPresent() || q.get().trim().isEmpty()) {
            model.addAttribute("message", "Please enter a valid search keyword.");
            model.addAttribute("results", Collections.emptyList());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", 0);
            return "searchresult";
        }

        try {
            String query = URLEncoder.encode(q.orElse(""), StandardCharsets.UTF_8.toString());
            String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/books/v1/volumes/")
                    .queryParam("q", query)
                    .queryParam("startIndex", (currentPage - 1) * booksPerPage)
                    .queryParam("maxResults", booksPerPage)
                    .queryParam("key", apiKey)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            String res = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(res);
            List<BookDTO> books = new ArrayList<>();

            for (JsonNode item : root.path("items")) {
                BookDTO book = new BookDTO();
                book.setTitle(item.path("volumeInfo").path("title").asText());
                book.setBookID(item.path("id").asText());
                book.setAverageRating(item.path("volumeInfo").path("averageRating").asDouble(0.0));
                book.setThumbnail(item.path("volumeInfo").path("imageLinks").path("thumbnail").asText());
                books.add(book);
            }

            int totalItems = root.path("totalItems").asInt();
            PaginationInfo paginationInfo = PaginationUtils.calculatePagination(currentPage, totalItems, booksPerPage,
                    pageGroupSize);

            model.addAttribute("results", books);
            model.addAttribute("query", q.orElse(""));
            model.addAttribute("currentPage", paginationInfo.getCurrentPage());
            model.addAttribute("currentGroupEnd", paginationInfo.getCurrentGroupEnd());
            model.addAttribute("currentGroupStart", paginationInfo.getCurrentGroupStart());
            model.addAttribute("hasMorePages", paginationInfo.isHasMorePages());
            model.addAttribute("totalPages", paginationInfo.getTotalPages());
            model.addAttribute("isNotFirstPage", paginationInfo.isNotFirstPage());
            model.addAttribute("totalItems", paginationInfo.getTotalItems());

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("results", "error");
        }
        return "searchresult";
    }
}
