package com.helpdesk.helpdesk.dto;

/*
 DTO = Data Transfer Object
 Used to transfer data between Controller and Service layers
 Helps avoid exposing Entity (database structure) directly

 ResponseDTO → Output (backend → client)
 This controls what data is exposed in API response
*/

import java.util.Map;

public class TicketResponseDTO {

    private Long id;
    private String employeeName;
    private String issue;
    private String category;
    private String status;

    // 🤖 AI Generated Response
    private String response;
    private String priority;

    private String message;
    private String toolName;
    private Map<String, Object> arguments;
    private boolean isToolCall;
    // ===== GETTERS =====

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getIssue() {
        return issue;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}