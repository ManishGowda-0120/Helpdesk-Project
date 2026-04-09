package com.helpdesk.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.helpdesk.dto.ChatRequestDTO;
import com.helpdesk.helpdesk.dto.ChatResponseDTO;
import com.helpdesk.helpdesk.service.AIService;
import com.helpdesk.helpdesk.service.ai.ToolExecutor;
import com.helpdesk.helpdesk.service.ai.ToolRegistry;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ToolRegistry toolRegistry;

    @Autowired
    private ToolExecutor toolExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> chat(@Valid @RequestBody ChatRequestDTO dto) {
        try {
            // ✅ Auto-fill employeeName from JWT token
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            dto.setEmployeeName(username);

            // Call AI with MCP tools
            Map response = aiService.chatWithTools(
                    dto.getMessage(),
                    toolRegistry.getTools()
            );

            Map<String, Object> result = aiService.extractToolCall(response);

            if ((Boolean) result.get("isToolCall")) {
                // ✅ AI decided to call a tool (e.g. create_ticket)
                String toolName = result.get("toolName").toString();
                String argsJson = result.get("arguments").toString();

                Map<String, Object> args = objectMapper.readValue(argsJson, Map.class);
                args.put("employeeName", username); // inject logged-in user

                Object output = toolExecutor.execute(toolName, args);

                return ResponseEntity.ok(
                        new ChatResponseDTO("✅ Done: " + toolName, output)
                );
            }

            // ✅ AI replied with plain text
            return ResponseEntity.ok(
                    new ChatResponseDTO(result.get("message").toString())
            );

        } catch (Exception e) {
            e.printStackTrace(); // print full error in IntelliJ console
            return ResponseEntity.internalServerError()
                    .body(new ChatResponseDTO("Error: " + e.getMessage()));
        }
    }
}