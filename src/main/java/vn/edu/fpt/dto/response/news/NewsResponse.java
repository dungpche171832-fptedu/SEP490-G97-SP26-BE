package vn.edu.fpt.dto.response.news;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NewsResponse {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;

    private Boolean isActive;
    private Integer displayOrder;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}