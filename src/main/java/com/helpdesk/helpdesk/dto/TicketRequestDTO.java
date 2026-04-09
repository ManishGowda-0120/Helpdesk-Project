package com.helpdesk.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating/updating a ticket.
 * employeeName is NO LONGER sent by the client — it is injected
 * from the JWT token by the controller (Fix #4).
 */
public class TicketRequestDTO {

    // Set by the controller from the JWT, never from request body
    private String employeeName;

    @NotBlank(message = "Issue is required")
    private String issue;

    private String status;

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
