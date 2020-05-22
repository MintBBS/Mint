package com.example.mint;

public class Login {
    private String agentId;
    private String password;
    private String mobileNumber;

    public Login() {
    }

    public Login(String agentId, String password, String mobileNumber) {
        this.agentId = agentId;
        this.password = password;
        this.mobileNumber = mobileNumber;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
