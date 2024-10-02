package com.wevioo.validator.service;

import com.wevioo.validator.domain.entity.depositor.Depositor;
import com.wevioo.validator.domain.entity.result.DepositorValidationResult;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class DepositorValidatorService {


    @Autowired
    private KieContainer kieContainer;
    @Autowired
    private CachingService cachingService;

    // Method to validate a list of depositors
    public List<DepositorValidationResult> validateDepositors(List<Depositor> depositors) {

        List<DepositorValidationResult> results = new ArrayList<>();

        // Create a KieSession from rules loaded from the database
        // KieSession kieSession = droolsEngineService.createKieSessionFromRules();

        KieSession kieSession =  kieContainer.newKieSession();

        // Fetch lists from services
        List<String> countries = cachingService.getCountries();  // Returns List of country codes/names
        List<String> currencies = cachingService.getCurrencies();  // Returns List of currency codes

        // Inject the lists as global variables
        kieSession.setGlobal("countries", countries);
        kieSession.setGlobal("currencies", currencies);

        try {
            for (Depositor depositor : depositors) {
                // Create a validation result object for each depositor
                DepositorValidationResult validationResult = new DepositorValidationResult();
                validationResult.setDpoId(depositor.getDpoId());  // Set dpoId for tracking

                // Set the global validationResult object in the KieSession
                kieSession.setGlobal("validationResult", validationResult);

                // Insert each depositor into the KieSession
                kieSession.insert(depositor);

                // Fire all rules in the .drl file
                kieSession.fireAllRules();

                // Add the result for each depositor to the list of results
                results.add(validationResult);
            }
        } finally {
            // Dispose the session after all rule execution
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
        return results;  // Return the list of validation results
    }


}
