package com.example.mint;

public class Pmjjby {
    private String aadhar_number,nominee_aadhar;
    private String account_number,dateofbirth;
    private String nominee_name,nominee_relation;
    private int scheme_id;


    public Pmjjby(String aadhar_number,String account_number, String nominee_aadhar, String nominee_name, String nominee_relation,  String dateofbirth) {
        this.aadhar_number = aadhar_number;
        this.account_number = account_number;
        this.nominee_aadhar = nominee_aadhar;
        this.nominee_name = nominee_name;
        this.nominee_relation = nominee_relation;
        this.dateofbirth = dateofbirth;

    }

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

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
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

    public int getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(int scheme_id) {
        this.scheme_id = scheme_id;
    }
}
