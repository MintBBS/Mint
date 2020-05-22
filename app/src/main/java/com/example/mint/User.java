package com.example.mint;

public class User {
        private String agentId;
        private String password;
        private String mobileNumber;
        private String fingerprint;

        public User() {
        }

        public User(String agentId, String password, String mobileNumber, String fingerprint) {
            this.agentId = agentId;
            this.password = password;
            this.mobileNumber = mobileNumber;
            this.fingerprint = fingerprint;
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

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

    }
