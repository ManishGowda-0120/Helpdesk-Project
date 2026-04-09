package com.helpdesk.helpdesk.repository;

import com.helpdesk.helpdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // ✅ Employee filtering - finds tickets by employee name

    List<Ticket> findByEmployeeName(String employeeName);
}
    /* JpaRepository gives you automatically:
1.save()
2.findAll()
3.findById()
4.delete() */

