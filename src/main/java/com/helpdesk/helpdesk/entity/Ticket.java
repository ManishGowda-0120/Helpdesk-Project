package com.helpdesk.helpdesk.entity;

import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;

    @Column(columnDefinition = "TEXT")  // ✅ issue can be long
    private String issue;

    private String category;   // short (Hardware/Software/Network/Access)
    private String status;
    private String priority;   // short (HIGH/MEDIUM/LOW)

    @Column(columnDefinition = "TEXT")  // ✅ AI response can be long
    private String response;
    public static final String OPEN = "OPEN";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String RESOLVED = "RESOLVED";
    public Ticket() {}

    // ===== GETTERS =====

    public Long getId() { return id; }
    public String getEmployeeName() { return employeeName; }
    public String getIssue() { return issue; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getResponse() { return response; }

    // ===== SETTERS =====

    public void setId(Long id) { this.id = id; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setIssue(String issue) { this.issue = issue; }
    public void setCategory(String category) { this.category = category; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setResponse(String response) { this.response = response; }
}