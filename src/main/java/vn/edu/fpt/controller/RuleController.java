package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.rule.ReplaceRuleByCarTypeRequest;
import vn.edu.fpt.dto.response.rule.RuleListResponse;
import vn.edu.fpt.service.rule.RuleService;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @PutMapping("/replace")
    public ResponseEntity<RuleListResponse> replaceRulesByCarType(
            @Valid @RequestBody ReplaceRuleByCarTypeRequest request
    ) {
        return ResponseEntity.ok(ruleService.replaceRulesByCarType(request));
    }

    @GetMapping
    public ResponseEntity<RuleListResponse> getRulesByCarType(@RequestParam String carType) {
        return ResponseEntity.ok(ruleService.getRulesByCarType(carType));
    }
}