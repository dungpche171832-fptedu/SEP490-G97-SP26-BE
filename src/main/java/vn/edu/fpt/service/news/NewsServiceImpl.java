package vn.edu.fpt.service.news;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.news.NewsRequest;
import vn.edu.fpt.dto.response.news.NewsResponse;
import vn.edu.fpt.entity.News;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.NewsRepository;
import vn.edu.fpt.ultis.errorCode.NewsErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public NewsResponse create(NewsRequest request) {

        validate(request);

        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive())
                .displayOrder(request.getDisplayOrder())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isDeleted(false)
                .build();

        return map(newsRepository.save(news));
    }

    @Override
    public NewsResponse update(Long id, NewsRequest request) {

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(NewsErrorCode.NEWS_NOT_FOUND));

        validate(request);

        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setIsActive(request.getIsActive());
        news.setDisplayOrder(request.getDisplayOrder());
        news.setStartTime(request.getStartTime());
        news.setEndTime(request.getEndTime());

        return map(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new AppException(NewsErrorCode.NEWS_NOT_FOUND));

        news.setIsDeleted(true);
        newsRepository.save(news);
    }

    @Override
    public List<NewsResponse> getAll() {
        return newsRepository.findByIsDeletedFalse()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<NewsResponse> getActive() {
        return newsRepository.findActiveNews(LocalDateTime.now())
                .stream()
                .map(this::map)
                .toList();
    }

    // ===== VALIDATE =====
    private void validate(NewsRequest request) {

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new AppException(NewsErrorCode.TITLE_REQUIRED);
        }

        if (request.getImageUrl() == null || request.getImageUrl().isBlank()) {
            throw new AppException(NewsErrorCode.IMAGE_REQUIRED);
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new AppException(NewsErrorCode.INVALID_DISPLAY_ORDER);
        }

        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new AppException(NewsErrorCode.INVALID_TIME_RANGE);
            }
        }
    }

    // ===== MAP =====
    private NewsResponse map(News n) {
        return NewsResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .imageUrl(n.getImageUrl())
                .isActive(n.getIsActive())
                .displayOrder(n.getDisplayOrder())
                .startTime(n.getStartTime())
                .endTime(n.getEndTime())
                .build();
    }
}
