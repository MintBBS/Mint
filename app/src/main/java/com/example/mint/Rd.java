package com.example.mint;

public class Rd {

    private String aadhar_number,nominee_aadhar;
    private String account_number;
    private String nominee_name,nominee_relation;
    private String amount,installment_amount,dateofbirth;
    private int scheme_id;

    public Rd(String aadhar_number, String account_number, String dateofbirth,  String nominee_aadhar,String nominee_name,
              String nominee_relation, String amount, String installment_amount) {
        this.aadhar_number = aadhar_number;

        this.account_number = account_number;
        this.dateofbirth = dateofbirth;
        this.nominee_aadhar = nominee_aadhar;
        this.nominee_name = nominee_name;
        this.nominee_relation = nominee_relation;
        this.amount = amount;
        this.installment_amount = installment_amount;



    }

    public Rd(){}


    public String getAadhar_number() {
        return aadhar_number;
    }

    public void setAadhar_number(String aadhar_number) {
        this.aadhar_number = aadhar_number;
    }

    public String getNominee_aadhar() {
        return nominee_aadhar;
    }

    public void setNominee_aadhar(String nominee_aadhar) {
        this.nominee_aadhar = nominee_aadhar;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getNominee_name() {
        return nominee_name;
    }

    public void setNominee_name(String nominee_name) {
        this.nominee_name = nominee_name;
    }

    public String getNominee_relation() {
        return nominee_relation;
    }

    public void setNominee_relation(String nominee_relation) {
        this.nominee_relation = nominee_relation;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(String installment_amount) {
        this.installment_amount = installment_amount;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public int getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(int scheme_id) {
        this.scheme_id = scheme_id;
    }
}
