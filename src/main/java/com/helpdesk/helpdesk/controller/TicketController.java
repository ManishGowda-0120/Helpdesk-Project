package com.helpdesk.helpdesk.controller;

import com.helpdesk.helpdesk.dto.TicketRequestDTO;
import com.helpdesk.helpdesk.dto.TicketResponseDTO;
import com.helpdesk.helpdesk.dto.UpdateStatusDTO;
import com.helpdesk.helpdesk.entity.Ticket;
import com.helpdesk.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // ── 1. Create Ticket — employee name auto-filled from JWT ──────────
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO dto) {
        // FIX #4: inject logged-in username, ignore whatever client sent
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        dto.setEmployeeName(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(dto));
    }

    // ── 2. Get Tickets — Admin sees all, Employee sees only theirs ──────
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role     = auth.getAuthorities().iterator().next().getAuthority();
        String username = auth.getName();

        if (role.equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(ticketService.getAllTickets());
        } else {
            return ResponseEntity.ok(ticketService.getTicketsByEmployee(username));
        }
    }

    // ── 3. Admin Dashboard Stats ───────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(ticketService.getDashboardStats());
    }

    // ── 4. Get Ticket by ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    // ── 5. Update Ticket ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDTO dto) {
        // Keep employee name locked to original owner on update
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        dto.setEmployeeName(username);
        return ResponseEntity.ok(ticketService.updateTicket(id, dto));
    }

    // ── 6. Delete Ticket ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Ticket deleted successfully");
    }

    // ── 7. Update Status (Admin only) ──────────────────────────────────
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusDTO dto) {
        return ResponseEntity.ok(ticketService.updateStatus(id, dto.getStatus()));
    }
}
