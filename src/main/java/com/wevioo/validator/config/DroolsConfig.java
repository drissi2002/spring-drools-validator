package com.wevioo.validator.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wevioo.validator.domain.entity.rule.DroolsRule;
import com.wevioo.validator.repository.DroolsRuleRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DroolsConfig {

    @Autowired
    private DroolsRuleRepository droolsRuleRepository;
    private static final String TEMP_RULES_FILE_PATH = "rules/depositor-rules.drl";
    private static final KieServices kieServices = KieServices.Factory.get();

    @Bean
    public KieContainer kieContainer() throws IOException {
        String droolsRules = loadDroolsRules();
        File tempFile = createTempDroolsFile(droolsRules);

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newFileResource(tempFile));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        // Clean up: delete the temporary file after loading the rules
        deleteTempFile(tempFile);

        return kieContainer;
    }

    // Load the Drools rules from the database
    private String loadDroolsRules() {
        StringBuilder droolsRulesBuilder = new StringBuilder();

        // Retrieve and append the import rule if it exists
        droolsRuleRepository.findByRuleName("imports*")
                .ifPresent(importRule -> droolsRulesBuilder.append(importRule.getRuleText()).append("\n\n"));

        // Retrieve all other rules excluding the import rule
        List<DroolsRule> otherRules = droolsRuleRepository.findAll().stream()
                .filter(rule -> !"import*".equals(rule.getRuleName()))
                .collect(Collectors.toList());

        // Append the other rules
        otherRules.forEach(rule ->
                droolsRulesBuilder.append(rule.getRuleText()).append("\n\n")
        );

        return droolsRulesBuilder.toString();
    }

    // Write the rule content to a temporary .drl file
    private File createTempDroolsFile(String droolsRules) throws IOException {
        File tempFile = new File(TEMP_RULES_FILE_PATH);
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs(); // Create the directory if it doesn't exist
        }
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(droolsRules);
        }
        return tempFile;
    }

    // Delete the temporary file after usage
    private void deleteTempFile(File file) {
        try {
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging the error instead
        }
    }
}


