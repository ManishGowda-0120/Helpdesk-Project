package com.helpdesk.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;

public class TicketRequestDTO {

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Email is required")
    private String employeeEmail; // ✅ new field

    @NotBlank(message = "Issue is required")
    private String issue;

    private String status;

    public String getEmployeeName()  { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }

    public String getIssue()  { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}