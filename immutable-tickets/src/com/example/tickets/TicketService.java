package com.example.tickets;

/**
 * Service layer that creates and "updates" incident tickets.
 *
 * Since IncidentTicket is now immutable, "updates" (like escalation or
 * assignment) are modelled by creating a NEW ticket through the Builder's
 * copy-and-modify pattern (toBuilder / Builder.from).
 *
 * Validation lives entirely in IncidentTicket.Builder.build() —
 * nothing is duplicated here.
 */
public class TicketService {

    /**
     * Creates a brand-new ticket with the three required fields plus
     * sensible defaults for source, priority, and customerVisible.
     */
    public IncidentTicket createTicket(String id, String reporterEmail, String title) {
        return new IncidentTicket.Builder()
                .id(id)
                .reporterEmail(reporterEmail)
                .title(title)
                .priority("MEDIUM")
                .source("CLI")
                .customerVisible(false)
                .addTag("NEW")
                .build();
    }

    /**
     * Returns a NEW ticket with priority CRITICAL and tag ESCALATED.
     * The original ticket is never touched.
     */
    public IncidentTicket escalateToCritical(IncidentTicket ticket) {
        return ticket.toBuilder()
                     .priority("CRITICAL")
                     .addTag("ESCALATED")
                     .build();
    }

    /**
     * Returns a NEW ticket with the given assignee set.
     * The original ticket is never touched.
     */
    public IncidentTicket assign(IncidentTicket ticket, String assigneeEmail) {
        return ticket.toBuilder()
                     .assigneeEmail(assigneeEmail)
                     .build();
    }
}
