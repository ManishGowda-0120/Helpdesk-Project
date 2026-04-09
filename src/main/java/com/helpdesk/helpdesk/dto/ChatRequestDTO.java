package com.helpdesk.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequestDTO {

    @NotBlank(message = "Message cannot be empty")
    private String message;
    private String employeeName;

    public String getMessage() {
        return message;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}