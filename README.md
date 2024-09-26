# Drools Rule Validator
This project implements a dynamic rule validation system using Drools in a Spring Boot application. 
The primary goal of this application is to validate depositors against a set of dynamic business rules stored in a database.

## Drools rules file stored in the database for dynamic rule management and validation
```bash
package com.wevioo.validator;

import com.wevioo.validator.domain.entity.depositor.Depositor;
import com.wevioo.validator.domain.entity.result.DepositorValidationResult;
import com.wevioo.validator.domain.entity.depositor.AccountsIdentification;
import com.wevioo.validator.domain.entity.depositor.Account;
global com.wevioo.validator.domain.entity.result.DepositorValidationResult validationResult;

dialect "mvel"

// Rule to validate CIN number length
rule "Validate CIN Number"
when
    $depositor: Depositor(depositorIdentificationPP != null)
then
    String cinNum = $depositor.getDepositorIdentificationPP().getCinNum();
    if (cinNum == null || cinNum.length() != 8) {
        validationResult.addError("cinNum", "Invalid CIN number for dpoId: " + $depositor.getDpoId());
    }
end

// Rule to check if the CIN issue date is in the future
rule "Validate CIN Issue Date"
when
    $depositor: Depositor(depositorIdentificationPP != null)
then
    if (isDateInFuture($depositor.getDepositorIdentificationPP().getCinIssueDate())) {
        validationResult.addError("cinIssueDate", "CIN issue date is in the future for dpoId: " + $depositor.getDpoId());
    }
end

// Rule to validate if the depositors age is over 18
rule "Validate Depositor Age Over 18"
when
    $depositor: Depositor(depositorIdentificationPP != null)
then
    if (isUnderAge($depositor.getDepositorIdentificationPP().getBirthDay())) {
        validationResult.addError("birthDay", "Depositor is under 18 years old for dpoId: " + $depositor.getDpoId());
    }
end

// Rule to ensure depositorIdentificationPP and depositorIdentificationPM are mutually exclusive
rule "Mutually Exclusive PP and PM Identification"
when
    $depositor: Depositor(depositorIdentificationPP != null && depositorIdentificationPM != null)
then
    validationResult.addError("depositorIdentification", "Both PP and PM identification present for dpoId: " + $depositor.getDpoId());
end

// Rule to validate compensable aggregate balance
rule "Validate Compensable Aggregate Balance"
when
    $depositor: Depositor($accountIdentification: accountsIdentification != null)
    $account: Account(accountBalance != null, compensableAggregateBalance != null) from $accountIdentification.getAccount()
    eval(Double.parseDouble($account.getCompensableAggregateBalance()) > Double.parseDouble($account.getAccountBalance()))
then
    validationResult.addError("compensableAggregateBalance", "Compensable aggregate balance exceeds account balance for dpoId: " + $depositor.getDpoId());
end

// Rule to check if bank card has expired
rule "Validate Bank Card Expiry"
when
    $depositor: Depositor($accountIdentification: accountsIdentification != null)
    $account: Account(bankCardValidity != null) from $accountIdentification.getAccount()
then
    if (isCardExpired($account.getBankCardValidity())) {
        validationResult.addError("bankCardValidity", "Bank card has expired for dpoId: " + $depositor.getDpoId());
    }
end

// Function to check if a date is in the future
function boolean isDateInFuture(String dateStr) {
    java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
    java.time.LocalDate today = java.time.LocalDate.now();
    return date.isAfter(today);
}

// Function to calculate age from birth date
function boolean isUnderAge(String birthDate) {
    java.time.LocalDate birth = java.time.LocalDate.parse(birthDate);
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.Period age = java.time.Period.between(birth, today);
    return age.getYears() < 18;
}

// Function to check if a bank card has expired
function boolean isCardExpired(String expiryDateStr) {
    java.time.YearMonth expiry = java.time.YearMonth.parse(expiryDateStr, java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
    java.time.YearMonth today = java.time.YearMonth.now();
    return expiry.isBefore(today);
}

```
## Overview of how the Drools rules file is stored in the database
![image](https://github.com/user-attachments/assets/34dcc7ef-8d64-4cfa-86b6-5014c6413516)

