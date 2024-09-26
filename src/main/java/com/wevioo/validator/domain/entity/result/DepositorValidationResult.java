package com.wevioo.validator.domain.entity.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositorValidationResult {
    private String dpoId;
    // Use a Map to store field name as the key and a list of errors as the value
    private Map<String, List<String>> fieldErrors = new HashMap<>();

    // Method to add errors related to a specific field
    public void addError(String fieldName, String errorMessage) {
        fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
    }

    // Method to get all errors related to a specific field
    public List<String> getErrorsForField(String fieldName) {
        return fieldErrors.getOrDefault(fieldName, new ArrayList<>());
    }

    // Method to get all field errors as a map
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
}