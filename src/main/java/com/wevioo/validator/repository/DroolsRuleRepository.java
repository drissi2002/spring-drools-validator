package com.wevioo.validator.repository;

import com.wevioo.validator.domain.entity.rule.DroolsRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DroolsRuleRepository extends JpaRepository<DroolsRule, Long> {

    // Method to find the rule with the name "import*"
    Optional<DroolsRule> findByRuleName(String ruleName);
}