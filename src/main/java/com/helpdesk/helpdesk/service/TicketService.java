package com.helpdesk.helpdesk.service;

import com.helpdesk.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.helpdesk.dto.TicketResponseDTO;
import com.helpdesk.helpdesk.entity.Ticket;
import com.helpdesk.helpdesk.exception.TicketNotFoundException;
import com.helpdesk.helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private EmailService emailService;

    // 1. Create Ticket
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        Ticket ticket = mapToEntity(dto);
        ticket.setCategory(aiService.suggestCategory(dto.getIssue()));
        ticket.setResponse(aiService.generateResponse(dto.getIssue()));
        ticket.setPriority(aiService.suggestPriority(dto.getIssue()));
        Ticket saved = ticketRepository.save(ticket);
        emailService.sendTicketCreatedEmail(saved);
        return mapToResponseDTO(saved);
    }

    // 2. Get All Tickets (Admin)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // 3. Get tickets by employee name (Employee)
    public List<Ticket> getTicketsByEmployee(String employeeName) {
        return ticketRepository.findByEmployeeName(employeeName);
    }

    // 4. Get Ticket by ID
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
    }

    // 5. Update Ticket
    public TicketResponseDTO updateTicket(Long id, TicketRequestDTO dto) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
        existing.setEmployeeName(dto.getEmployeeName());
        existing.setIssue(dto.getIssue());
        existing.setStatus(dto.getStatus());
        existing.setCategory(aiService.suggestCategory(dto.getIssue()));
        existing.setResponse(aiService.generateResponse(dto.getIssue()));
        existing.setPriority(aiService.suggestPriority(dto.getIssue()));
        Ticket updated = ticketRepository.save(existing);
        emailService.sendTicketUpdatedEmail(updated);
        return mapToResponseDTO(updated);
    }

    // 6. Delete Ticket
    public void deleteTicket(Long id) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(existing);
    }

    // 7. Update Status
    public Ticket updateStatus(Long id, String newStatus) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        String current = ticket.getStatus();
        if (current.equals("OPEN") && newStatus.equals("IN_PROGRESS")) {
            ticket.setStatus(newStatus);
        } else if (current.equals("IN_PROGRESS") && newStatus.equals("RESOLVED")) {
            ticket.setStatus(newStatus);
        } else {
            throw new RuntimeException("Invalid transition: " + current + " → " + newStatus);
        }
        return ticketRepository.save(ticket);
    }

    // 8. ✅ Dashboard Stats - FIXED null handling
    public Map<String, Object> getDashboardStats() {
        List<Ticket> all = ticketRepository.findAll();

        Map<String, Object> stats = new HashMap<>();

        // Total
        stats.put("total", all.size());

        // By Status
        Map<String, Long> byStatus = all.stream()
                .filter(t -> t.getStatus() != null)          // ✅ skip nulls
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));
        stats.put("byStatus", byStatus);
        stats.put("open",       byStatus.getOrDefault("OPEN", 0L));
        stats.put("inProgress", byStatus.getOrDefault("IN_PROGRESS", 0L));
        stats.put("resolved",   byStatus.getOrDefault("RESOLVED", 0L));

        // By Category - ✅ replace null with "Unknown"
        Map<String, Long> byCategory = all.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "Unknown",
                        Collectors.counting()
                ));
        stats.put("byCategory", byCategory);

        // By Priority - ✅ replace null with "Unknown"
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getPriority() != null ? t.getPriority() : "Unknown",
                        Collectors.counting()
                ));
        stats.put("byPriority", byPriority);

        // By Employee - ✅ replace null with "Unknown"
        Map<String, Long> byEmployee = all.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getEmployeeName() != null ? t.getEmployeeName() : "Unknown",
                        Collectors.counting()
                ));
        stats.put("byEmployee", byEmployee);

        return stats;
    }

    // ===== MAPPING =====
    private Ticket mapToEntity(TicketRequestDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setEmployeeName(dto.getEmployeeName());
        ticket.setIssue(dto.getIssue());
        ticket.setStatus("OPEN");
        return ticket;
    }

    private TicketResponseDTO mapToResponseDTO(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setEmployeeName(ticket.getEmployeeName());
        dto.setIssue(ticket.getIssue());
        dto.setCategory(ticket.getCategory());
        dto.setStatus(ticket.getStatus());
        dto.setResponse(ticket.getResponse());
        dto.setPriority(ticket.getPriority());
        return dto;
    }
}