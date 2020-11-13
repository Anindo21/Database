package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public class SupportedProjects {
    private int id;
    private String title;
    private String status;
    private String icon;
    private java.math.BigDecimal limit;
    private java.math.BigDecimal amount;

    public SupportedProjects(int id, String titel, String status, String icon, BigDecimal limit, BigDecimal amount) {
        this.id = id;
        this.title = titel;
        this.status = status;
        this.icon = icon;
        this.limit = limit;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getIcon() {
        return icon;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
