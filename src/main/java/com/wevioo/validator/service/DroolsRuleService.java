package com.wevioo.validator.service;

import com.wevioo.validator.domain.entity.rule.DroolsRule;
import com.wevioo.validator.repository.DroolsRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DroolsRuleService {

    @Autowired
    private DroolsRuleRepository droolsRuleRepository;

    public List<String> getAllRules() {
        return droolsRuleRepository.findAll().stream()
                .map(DroolsRule::getRuleText)
                .collect(Collectors.toList());
    }
}
