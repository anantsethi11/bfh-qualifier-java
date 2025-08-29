package com.bfh.qualifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bfh")
public class Settings {
    private String name;
    private String regNo;
    private String email;
    private Endpoints endpoints = new Endpoints();
    private boolean preferReturnedWebhook = true;
    private Sql sql = new Sql();

    public static class Endpoints {
        private String generateWebhook;
        private String fallbackSubmit;
        public String getGenerateWebhook() { return generateWebhook; }
        public void setGenerateWebhook(String generateWebhook) { this.generateWebhook = generateWebhook; }
        public String getFallbackSubmit() { return fallbackSubmit; }
        public void setFallbackSubmit(String fallbackSubmit) { this.fallbackSubmit = fallbackSubmit; }
    }

    public static class Sql {
        private String odd;
        private String even;
        public String getOdd() { return odd; }
        public void setOdd(String odd) { this.odd = odd; }
        public String getEven() { return even; }
        public void setEven(String even) { this.even = even; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Endpoints getEndpoints() { return endpoints; }
    public void setEndpoints(Endpoints endpoints) { this.endpoints = endpoints; }
    public boolean isPreferReturnedWebhook() { return preferReturnedWebhook; }
    public void setPreferReturnedWebhook(boolean preferReturnedWebhook) { this.preferReturnedWebhook = preferReturnedWebhook; }
    public Sql getSql() { return sql; }
    public void setSql(Sql sql) { this.sql = sql; }
}
