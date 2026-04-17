package vn.edu.fpt.dto.response.rule;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleListResponse {

    private List<RuleResponse> rules;
    private String message;
    private int totalCount;
}