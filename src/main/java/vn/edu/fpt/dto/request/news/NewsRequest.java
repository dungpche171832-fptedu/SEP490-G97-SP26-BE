package vn.edu.fpt.dto.request.news;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsRequest {

    private String title;
    private String content;
    private String imageUrl;

    private Boolean isActive;
    private Integer displayOrder;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}