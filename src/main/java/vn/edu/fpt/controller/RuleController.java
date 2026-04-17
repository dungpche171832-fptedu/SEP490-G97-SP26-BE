package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.rule.ReplaceRuleByCarTypeRequest;
import vn.edu.fpt.dto.response.rule.PriceResponse;
import vn.edu.fpt.dto.response.rule.RuleListResponse;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.service.rule.RuleService;

import java.math.BigDecimal;

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

    @GetMapping("/price")
    public ResponseEntity<PriceResponse> getPriceByDistanceAndCarType(
            @RequestParam String carType,
            @RequestParam BigDecimal distance,
            @RequestParam int totalSeat){

        try {
            BigDecimal price = ruleService.getPriceByDistanceAndCarType(carType, distance, totalSeat);
            PriceResponse response = new PriceResponse(price,totalSeat);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity.status(400).body(new PriceResponse(BigDecimal.ZERO, 0));
        }
    }
}