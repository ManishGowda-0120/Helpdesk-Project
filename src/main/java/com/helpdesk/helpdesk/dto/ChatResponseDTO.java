package com.helpdesk.helpdesk.dto;

public class ChatResponseDTO {

    private String message;
    private Object data;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String message) {
        this.message = message;
    }

    public ChatResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}