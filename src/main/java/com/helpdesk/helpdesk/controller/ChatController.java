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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
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
            Map response = aiService.chatWithTools(
                    dto.getMessage(),
                    toolRegistry.getTools()
            );

            Map result = aiService.extractToolCall(response);

            if ((Boolean) result.get("isToolCall")) {

                String toolName = result.get("toolName").toString();
                String argsJson = result.get("arguments").toString();

                Map<String, Object> args =
                        objectMapper.readValue(argsJson, Map.class);
                args.put("employeeName", dto.getEmployeeName()); // ✅ inject manually

                Object output = toolExecutor.execute(toolName, args);

                return ResponseEntity.ok(
                        new ChatResponseDTO("Action executed: " + toolName, output)

                );

            }

            return ResponseEntity.ok(
                    new ChatResponseDTO(result.get("message").toString())
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponseDTO("Error: " + e.getMessage()));

        }
    }
}