package com.wevioo.validator.service;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DroolsEngineService {

    @Autowired
    private DroolsRuleService droolsRuleService;

    public KieSession createKieSessionFromRules() {
        KieHelper kieHelper = new KieHelper();

        // Fetch all rules from the database
        List<String> rules = droolsRuleService.getAllRules();

        // Add each rule to KieHelper
        for (String rule : rules) {
            kieHelper.addContent(rule, ResourceType.DRL);
        }

        // Build KieBase and return a new KieSession
        KieBase kieBase = kieHelper.build();
        return kieBase.newKieSession();
    }
}