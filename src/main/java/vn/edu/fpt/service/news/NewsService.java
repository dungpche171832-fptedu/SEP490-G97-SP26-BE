package vn.edu.fpt.service.news;

import vn.edu.fpt.dto.request.news.NewsRequest;
import vn.edu.fpt.dto.response.news.NewsResponse;

import java.util.List;

public interface NewsService {

    NewsResponse create(NewsRequest request);

    NewsResponse update(Long id, NewsRequest request);

    void delete(Long id);

    List<NewsResponse> getAll(); // admin

    List<NewsResponse> getActive(); // user

    NewsResponse getNewsDetail(Long newsId);
}