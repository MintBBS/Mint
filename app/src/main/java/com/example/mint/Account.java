package com.example.mint;

public class Account {
    private String customerId;
    private String accountNumber;
    private String aadharNumber;
    private Double balance;
    private String ifscCode;
    private String dateOfOpening;
    private String nomineeName;
    private String relationship;
    private String nomineeDob;
    private String nomineeMinor;
    private String nomineeGuardianName;

    public Account() {
    }

    public Account(String customerId, String accountNumber, String aadharNumber, Double balance, String ifscCode, String dateOfOpening, String nomineeName, String relationship, String nomineeDob, String nomineeMinor, String nomineeGuardianName) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.aadharNumber = aadharNumber;
        this.balance = balance;
        this.ifscCode = ifscCode;
        this.dateOfOpening = dateOfOpening;
        this.nomineeName = nomineeName;
        this.relationship = relationship;
        this.nomineeDob = nomineeDob;
        this.nomineeMinor = nomineeMinor;
        this.nomineeGuardianName = nomineeGuardianName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getDateOfOpening() {
        return dateOfOpening;
    }

    public void setDateOfOpening(String dateOfOpening) {
        this.dateOfOpening = dateOfOpening;
    }

    public String getNomineeName() {
        return nomineeName;
    }

    public void setNomineeName(String nomineeName) {
        this.nomineeName = nomineeName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getNomineeDob() {
        return nomineeDob;
    }

    public void setNomineeDob(String nomineeDob) {
        this.nomineeDob = nomineeDob;
    }

    public String getNomineeMinor() {
        return nomineeMinor;
    }

    public void setNomineeMinor(String nomineeMinor) {
        this.nomineeMinor = nomineeMinor;
    }

    public String getNomineeGuardianName() {
        return nomineeGuardianName;
    }

    public void setNomineeGuardianName(String nomineeGuardianName) {
        this.nomineeGuardianName = nomineeGuardianName;
    }



}
