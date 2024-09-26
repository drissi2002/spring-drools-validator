package com.wevioo.validator.controller;

import com.wevioo.validator.domain.entity.depositor.Depositor;
import com.wevioo.validator.domain.entity.result.DepositorValidationResult;
import com.wevioo.validator.service.DepositorValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/depositors")
public class ValidatorController {

    @Autowired
    private DepositorValidatorService validatorService;

    @PostMapping("/validate")
    public ResponseEntity<List<DepositorValidationResult>> validateDepositors(@RequestBody List<Depositor> depositors) {
        List<DepositorValidationResult> results = validatorService.validateDepositors(depositors);
        return ResponseEntity.ok(results);
    }
}
