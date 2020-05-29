package com.example.mint;

class AccountDetails {
    private String name;
    private int age;
    private String dob;
    private String ifsc;
    private String aadhar;
    private String email;
    private String mobileNo;
    private String fatherName;
    private int annualIncome;
    private String address;
    private String nomineeName;
    private String relationship;
    private String nomineeAddress;


    public AccountDetails(String name, int age, String dob, String ifsc, String aadhar, String email, String mobileNo, String fatherName, int annualIncome, String address, String nomineeName, String relationship, String nomineeAddress) {
        this.name = name;
        this.age = age;
        this.dob = dob;
        this.ifsc = ifsc;
        this.aadhar = aadhar;
        this.email = email;
        this.mobileNo = mobileNo;
        this.fatherName = fatherName;
        this.annualIncome = annualIncome;
        this.address = address;
        this.nomineeName = nomineeName;
        this.relationship = relationship;
        this.nomineeAddress = nomineeAddress;
    }

    public AccountDetails(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public int getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(int annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getNomineeAddress() {
        return nomineeAddress;
    }

    public void setNomineeAddress(String nomineeAddress) {
        this.nomineeAddress = nomineeAddress;
    }

}
