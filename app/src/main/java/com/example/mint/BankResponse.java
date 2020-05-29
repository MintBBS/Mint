package com.example.mint;

class BankResponse {
    private AccountDetails accountDetails;
    private Integer status;

    public BankResponse(AccountDetails accountDetails, int status) {
        this.accountDetails = accountDetails;
        this.status = status;
    }

    public BankResponse() {
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
