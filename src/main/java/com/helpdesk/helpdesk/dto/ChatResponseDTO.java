package com.helpdesk.helpdesk.dto;

public class ChatResponseDTO {

    private String reply;
    private Object data; // optional - for tool call results

    // ✅ Constructor for plain text reply
    public ChatResponseDTO(String reply) {
        this.reply = reply;
        this.data = null;
    }

    // ✅ Constructor for tool call result
    public ChatResponseDTO(String reply, Object data) {
        this.reply = reply;
        this.data = data;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}