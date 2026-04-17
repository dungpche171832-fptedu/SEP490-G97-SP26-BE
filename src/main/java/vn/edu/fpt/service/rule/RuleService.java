package vn.edu.fpt.service.rule;

import vn.edu.fpt.dto.request.rule.ReplaceRuleByCarTypeRequest;
import vn.edu.fpt.dto.response.rule.RuleListResponse;

public interface RuleService {

    RuleListResponse replaceRulesByCarType(ReplaceRuleByCarTypeRequest request);

    RuleListResponse getRulesByCarType(String carType);
}