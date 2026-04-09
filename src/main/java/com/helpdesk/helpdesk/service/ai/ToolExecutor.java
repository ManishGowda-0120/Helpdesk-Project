package com.helpdesk.helpdesk.service.ai;

import com.helpdesk.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ToolExecutor {

    @Autowired
    private TicketService ticketService;

    public Object execute(String toolName, Map<String, Object> args) {

        switch (toolName) {

            case "create_ticket":

                String description = (String) args.get("description");
                String employeeName = (String) args.get("employeeName");

                TicketRequestDTO dto = new TicketRequestDTO();
                dto.setIssue(description);

                // ✅ Handle null name
                if (employeeName == null || employeeName.isBlank()) {
                    employeeName = "Guest";
                }

                dto.setEmployeeName(employeeName);

                return ticketService.createTicket(dto);

            case "get_all_tickets":
                return ticketService.getAllTickets();

            default:
                throw new RuntimeException("Unknown tool: " + toolName);
        }
    }
}