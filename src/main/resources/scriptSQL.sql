ALTER TABLE public.drools_rules ALTER COLUMN rule_text TYPE text;

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'imports *',  -- Setting rule_name to 'imports *'
    'package com.wevioo.validator;

    import com.wevioo.validator.domain.entity.depositor.Depositor;
    import java.util.ArrayList;
    import java.util.List;
    import com.wevioo.validator.domain.entity.result.DepositorValidationResult;
    import com.wevioo.validator.domain.entity.depositor.AccountsIdentification;
    import com.wevioo.validator.domain.entity.depositor.Account;
    global com.wevioo.validator.domain.entity.result.DepositorValidationResult validationResult;

    dialect "mvel";'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Validate CIN Number',  -- Setting rule_name for the new rule
    '// Rule to validate CIN number length
    rule "Validate CIN Number"
    when
        $depositor: Depositor(depositorIdentificationPP != null)
    then
        String cinNum = $depositor.getDepositorIdentificationPP().getCinNum();
        if (cinNum == null || cinNum.length() != 8) {
            validationResult.addError("cinNum", "Invalid CIN number for dpoId: " + $depositor.getDpoId());
        }
    end'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Validate CIN Issue Date',  -- Setting rule_name for the CIN issue date rule
    '// Rule to check if the CIN issue date is in the future
    rule "Validate CIN Issue Date"
    when
        $depositor: Depositor(depositorIdentificationPP != null)
    then
        if(isDateInFuture($depositor.getDepositorIdentificationPP().getCinIssueDate())) {
            validationResult.addError("cinIssueDate", "CIN issue date is in the future for dpoId: " + $depositor.getDpoId());
        }
    end

    // Function to check if a date is in the future
    function boolean isDateInFuture(String dateStr) {
        java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
        java.time.LocalDate today = java.time.LocalDate.now();
        return date.isAfter(today);
    }'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Validate Depositor Age Over 18',  -- Setting rule_name for the age validation rule
    '// Rule to validate if the depositor age is over 18
    rule "Validate Depositor Age Over 18"
    when
        $depositor: Depositor(depositorIdentificationPP != null)
    then
        if(isUnderAge($depositor.getDepositorIdentificationPP().getBirthDay())) {
            validationResult.addError("birthDay", "Depositor is under 18 years old for dpoId: " + $depositor.getDpoId());
        }
    end

    // Function to calculate age from birth date
    function boolean isUnderAge(String birthDate) {
        java.time.LocalDate birth = java.time.LocalDate.parse(birthDate);
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.Period age = java.time.Period.between(birth, today);
        return age.getYears() < 18;
    }'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Mutually Exclusive PP and PM Identification',  -- Setting rule_name for the mutual exclusion rule
    '// Rule to ensure depositorIdentificationPP and depositorIdentificationPM are mutually exclusive
    rule "Mutually Exclusive PP and PM Identification"
    when
        $depositor: Depositor(depositorIdentificationPP != null && depositorIdentificationPM != null)
    then
        validationResult.addError("depositorIdentification", "Both PP and PM identification present for dpoId: " + $depositor.getDpoId());
    end'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Validate Compensable Aggregate Balance',  -- Setting rule_name for the compensable aggregate balance validation rule
    '// Rule to validate compensable aggregate balance
    rule "Validate Compensable Aggregate Balance"
    when
        $depositor: Depositor($accountIdentification: accountsIdentification != null)
        $account: Account(accountBalance != null, compensableAggregateBalance != null) from $accountIdentification.getAccount()
        eval(Double.parseDouble($account.getCompensableAggregateBalance()) > Double.parseDouble($account.getAccountBalance()))
    then
        validationResult.addError("compensableAggregateBalance", "Compensable aggregate balance exceeds account balance for dpoId: " + $depositor.getDpoId());
    end'
);

INSERT INTO public.drools_rules (rule_name, rule_text)
VALUES (
    'Validate Bank Card Expiry',  -- Setting rule_name for the bank card expiry validation rule
    '// Rule to check if bank card has expired
    rule "Validate Bank Card Expiry"
    when
        $depositor: Depositor($accountIdentification: accountsIdentification != null)
        $account: Account(bankCardValidity != null) from $accountIdentification.getAccount()
    then
        if(isCardExpired($account.getBankCardValidity())) {
            validationResult.addError("bankCardValidity", "Bank card has expired for dpoId: " + $depositor.getDpoId());
        }
    end

    // Function to check if a bank card has expired
    function boolean isCardExpired(String expiryDateStr) {
        java.time.YearMonth expiry = java.time.YearMonth.parse(expiryDateStr, java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
        java.time.YearMonth today = java.time.YearMonth.now();
        return expiry.isBefore(today);
    }'
);


