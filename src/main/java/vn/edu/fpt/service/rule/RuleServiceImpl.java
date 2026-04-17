package vn.edu.fpt.service.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.rule.ReplaceRuleByCarTypeRequest;
import vn.edu.fpt.dto.request.rule.ReplaceRuleItemRequest;
import vn.edu.fpt.dto.response.rule.RuleListResponse;
import vn.edu.fpt.dto.response.rule.RuleResponse;
import vn.edu.fpt.entity.Rule;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.RuleRepository;
import vn.edu.fpt.ultis.enums.CarType;
import vn.edu.fpt.ultis.errorCode.RuleErrorCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;

    @Override
    @Transactional
    public RuleListResponse replaceRulesByCarType(ReplaceRuleByCarTypeRequest request) {
        if (request.getRules() == null || request.getRules().isEmpty()) {
            throw new AppException(RuleErrorCode.RULE_LIST_EMPTY);
        }

        validateRuleList(request.getRules());

        ruleRepository.deleteByCarType(request.getCarType());

        List<Rule> newRules = request.getRules().stream()
                .sorted(Comparator.comparing(ReplaceRuleItemRequest::getMinKm))
                .map(item -> Rule.builder()
                        .carType(request.getCarType())
                        .minKm(item.getMinKm())
                        .maxKm(item.getMaxKm())
                        .price(item.getPrice())
                        .build())
                .toList();

        List<Rule> savedRules = ruleRepository.saveAll(newRules);

        List<RuleResponse> responses = savedRules.stream()
                .sorted(Comparator.comparing(Rule::getMinKm))
                .map(this::mapToResponse)
                .toList();

        return RuleListResponse.builder()
                .rules(responses)
                .message("Cập nhật bộ rule thành công")
                .totalCount(responses.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RuleListResponse getRulesByCarType(String carType) {
        CarType parsedCarType;
        try {
            parsedCarType = CarType.valueOf(carType.trim().toUpperCase());
        } catch (Exception e) {
            throw new AppException(RuleErrorCode.RULE_NOT_FOUND);
        }

        List<Rule> rules = ruleRepository.findByCarTypeOrderByMinKmAsc(parsedCarType);

        if (rules.isEmpty()) {
            throw new AppException(RuleErrorCode.RULE_NOT_FOUND);
        }

        List<RuleResponse> responses = rules.stream()
                .map(this::mapToResponse)
                .toList();

        return RuleListResponse.builder()
                .rules(responses)
                .message("Danh sách rule")
                .totalCount(responses.size())
                .build();
    }

    private void validateRuleList(List<ReplaceRuleItemRequest> rules) {
        List<ReplaceRuleItemRequest> sortedRules = rules.stream()
                .sorted(Comparator.comparing(ReplaceRuleItemRequest::getMinKm))
                .toList();

        for (ReplaceRuleItemRequest rule : sortedRules) {
            if (rule.getMinKm() == null || rule.getMinKm().compareTo(BigDecimal.ZERO) < 0) {
                throw new AppException(RuleErrorCode.INVALID_MIN_KM);
            }

            if (rule.getPrice() == null || rule.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AppException(RuleErrorCode.INVALID_PRICE);
            }

            if (rule.getMaxKm() != null && rule.getMaxKm().compareTo(rule.getMinKm()) <= 0) {
                throw new AppException(RuleErrorCode.INVALID_MAX_KM);
            }
        }

        if (sortedRules.get(0).getMinKm().compareTo(BigDecimal.ZERO) != 0) {
            throw new AppException(RuleErrorCode.RULE_MUST_START_FROM_ZERO);
        }

        for (int i = 0; i < sortedRules.size() - 1; i++) {
            ReplaceRuleItemRequest current = sortedRules.get(i);
            ReplaceRuleItemRequest next = sortedRules.get(i + 1);

            if (current.getMaxKm() == null) {
                throw new AppException(RuleErrorCode.RULE_RANGE_NOT_CONTINUOUS);
            }

            if (current.getMaxKm().compareTo(next.getMinKm()) != 0) {
                throw new AppException(RuleErrorCode.RULE_RANGE_NOT_CONTINUOUS);
            }
        }

        if (sortedRules.get(sortedRules.size() - 1).getMaxKm() != null) {
            throw new AppException(RuleErrorCode.RULE_LAST_MAX_MUST_BE_NULL);
        }
    }

    private RuleResponse mapToResponse(Rule rule) {
        return RuleResponse.builder()
                .id(rule.getId())
                .carType(rule.getCarType())
                .minKm(rule.getMinKm())
                .maxKm(rule.getMaxKm())
                .price(rule.getPrice())
                .build();
    }
}