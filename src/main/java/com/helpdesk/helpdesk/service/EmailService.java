package com.helpdesk.helpdesk.service;

import com.helpdesk.helpdesk.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Send to employee's email when ticket is created
    public void sendTicketCreatedEmail(Ticket ticket) {
        try {
            String toEmail = ticket.getEmployeeEmail();
            if (toEmail == null || toEmail.isBlank()) return;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("✅ Ticket #" + ticket.getId() + " Created - IT Helpdesk");
            message.setText(
                    "Hi " + ticket.getEmployeeName() + ",\n\n" +
                            "Your ticket has been created successfully!\n\n" +
                            "🎫 Ticket ID  : #" + ticket.getId() + "\n" +
                            "📋 Issue      : " + ticket.getIssue() + "\n" +
                            "📁 Category   : " + ticket.getCategory() + "\n" +
                            "⚡ Priority   : " + ticket.getPriority() + "\n" +
                            "📌 Status     : " + ticket.getStatus() + "\n\n" +
                            "🤖 AI Response:\n" + ticket.getResponse() + "\n\n" +
                            "We will get back to you soon!\n\n" +
                            "IT Helpdesk Team"
            );
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
        }
    }

    // ✅ Send to employee's email when ticket is updated
    public void sendTicketUpdatedEmail(Ticket ticket) {
        try {
            String toEmail = ticket.getEmployeeEmail();
            if (toEmail == null || toEmail.isBlank()) return;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🔄 Ticket #" + ticket.getId() + " Updated - IT Helpdesk");
            message.setText(
                    "Hi " + ticket.getEmployeeName() + ",\n\n" +
                            "Your ticket has been updated!\n\n" +
                            "🎫 Ticket ID : #" + ticket.getId() + "\n" +
                            "📋 Issue     : " + ticket.getIssue() + "\n" +
                            "📌 Status    : " + ticket.getStatus() + "\n\n" +
                            "IT Helpdesk Team"
            );
            mailSender.send(message);
            System.out.println("✅ Update email sent to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
        }
    }
}