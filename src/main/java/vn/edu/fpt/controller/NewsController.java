package vn.edu.fpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.news.NewsRequest;
import vn.edu.fpt.dto.response.news.NewsResponse;
import vn.edu.fpt.service.news.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // ===== ADMIN =====

    @PostMapping
    public NewsResponse create(@RequestBody NewsRequest request) {
        return newsService.create(request);
    }

    @PutMapping("/{id}")
    public NewsResponse update(@PathVariable Long id,
                               @RequestBody NewsRequest request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }

    @GetMapping("/admin")
    public List<NewsResponse> getAll() {
        return newsService.getAll();
    }

    // ===== USER =====

    @GetMapping("/active")
    public List<NewsResponse> getActive() {
        return newsService.getActive();
    }

    @GetMapping("/{id}")
    public NewsResponse getNewsDetail(
            @PathVariable Long id
    ) {
        return newsService.getNewsDetail(id);
    }
}