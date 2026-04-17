package vn.edu.fpt.service.rule;

import vn.edu.fpt.dto.request.rule.ReplaceRuleByCarTypeRequest;
import vn.edu.fpt.dto.response.rule.RuleListResponse;

import java.math.BigDecimal;

public interface RuleService {

    RuleListResponse replaceRulesByCarType(ReplaceRuleByCarTypeRequest request);

    RuleListResponse getRulesByCarType(String carType);

    BigDecimal getPriceByDistanceAndCarType(String carType, BigDecimal distance);
}