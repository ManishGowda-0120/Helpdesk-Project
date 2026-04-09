package com.helpdesk.helpdesk.service.ai;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ToolRegistry {

    public List<Map<String, Object>> getTools() {
        return List.of(

                Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", "create_ticket",
                                "description", "Create a helpdesk ticket",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "title", Map.of("type", "string"),
                                                "description", Map.of("type", "string")
                                        ),
                                        "required", List.of("title", "description")
                                )
                        )
                ),

                Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", "get_all_tickets",
                                "description", "Get all tickets",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of()
                                )
                        )
                )

        );
    }
}