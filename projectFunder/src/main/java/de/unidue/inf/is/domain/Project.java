package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public class Project {
    private int id;
    private String title;
    private String name;
    private String status;
    private String icon;
    private java.math.BigDecimal sum;
    private String email;

    public Project(int id, String title, String name, String email, String status, String icon, BigDecimal sum) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.status = status;
        this.icon = icon;
        this.sum = sum;
        this.email = email;
    }

    public Project(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", icon='" + icon + '\'' +
                ", sum=" + sum +
                ", email='" + email + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getIcon() {
        return icon;
    }

    public BigDecimal getSum() {
        return sum;
    }
}
