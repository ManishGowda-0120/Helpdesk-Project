package com.helpdesk.helpdesk.service;

import com.helpdesk.helpdesk.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ─── Send email when ticket is CREATED ───────────────────────────
    public void sendTicketCreatedEmail(Ticket ticket) {
        String subject = "✅ Ticket #" + ticket.getId() + " Created - " + ticket.getCategory();

        String body = "Hello " + ticket.getEmployeeName() + ",\n\n" +
                "Your IT support ticket has been created successfully.\n\n" +
                "─────────────────────────────\n" +
                "Ticket ID   : #" + ticket.getId() + "\n" +
                "Issue       : " + ticket.getIssue() + "\n" +
                "Category    : " + ticket.getCategory() + "\n" +
                "Priority    : " + ticket.getPriority() + "\n" +
                "Status      : " + ticket.getStatus() + "\n" +
                "─────────────────────────────\n\n" +
                "AI Response :\n" + ticket.getResponse() + "\n\n" +
                "Our team will look into this shortly.\n\n" +
                "Regards,\n" +
                "IT Helpdesk Team";

        sendEmail(fromEmail, subject, body);
    }

    // ─── Send email when ticket is UPDATED ───────────────────────────
    public void sendTicketUpdatedEmail(Ticket ticket) {
        String subject = "🔄 Ticket #" + ticket.getId() + " Updated - Status: " + ticket.getStatus();

        String body = "Hello " + ticket.getEmployeeName() + ",\n\n" +
                "Your IT support ticket has been updated.\n\n" +
                "─────────────────────────────\n" +
                "Ticket ID   : #" + ticket.getId() + "\n" +
                "Issue       : " + ticket.getIssue() + "\n" +
                "Category    : " + ticket.getCategory() + "\n" +
                "Priority    : " + ticket.getPriority() + "\n" +
                "Status      : " + ticket.getStatus() + "\n" +
                "─────────────────────────────\n\n" +
                "AI Response :\n" + ticket.getResponse() + "\n\n" +
                "Regards,\n" +
                "IT Helpdesk Team";

        sendEmail(fromEmail, subject, body);
    }

    // ─── Common send method ───────────────────────────────────────────
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);  // sending to same email for testing
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("✅ Email sent: " + subject);
        } catch (Exception e) {
            // ✅ Don't crash the app if email fails
            System.err.println("❌ Email failed: " + e.getMessage());
        }
    }
}