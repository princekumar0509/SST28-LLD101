import com.example.tickets.IncidentTicket;
import com.example.tickets.TicketService;

import java.util.List;

/**
 * Demonstrates the refactored, immutable IncidentTicket.
 *
 * Key points shown here:
 *  1. Building a ticket through the fluent Builder API.
 *  2. "Updating" by creating a new ticket instance (original untouched).
 *  3. The tags list returned by getTags() is unmodifiable — external
 *     code cannot sneak mutations in via the reference.
 */
public class TryIt {

    public static void main(String[] args) {

        TicketService service = new TicketService();

        // --- 1. Create via service (uses Builder internally) ---
        IncidentTicket original = service.createTicket(
                "TCK-1001", "reporter@example.com", "Payment failing on checkout");
        System.out.println("Created  : " + original);
        System.out.println("Identity : " + System.identityHashCode(original));

        // --- 2. "Update" via service -> returns a NEW object ---
        IncidentTicket assigned   = service.assign(original, "agent@example.com");
        IncidentTicket escalated  = service.escalateToCritical(assigned);

        System.out.println("\nAfter assign    : " + assigned);
        System.out.println("After escalation: " + escalated);

        // Prove that 'original' was never changed
        System.out.println("\nOriginal (unchanged): " + original);
        System.out.println("All three are different objects? "
                + (System.identityHashCode(original) != System.identityHashCode(assigned)
                   && System.identityHashCode(assigned) != System.identityHashCode(escalated)));

        // --- 3. Tags list is unmodifiable – any attempt throws ---
        List<String> tags = escalated.getTags();
        System.out.println("\nEscalated tags: " + tags);
        try {
            tags.add("HACKED_FROM_OUTSIDE");
            System.out.println("ERROR: list was mutable! " + escalated.getTags());
        } catch (UnsupportedOperationException e) {
            System.out.println("Good: external mutation of tags blocked -> " + e.getClass().getSimpleName());
        }

        // --- 4. Manual builder usage with optional fields ---
        IncidentTicket detailed = new IncidentTicket.Builder()
                .id("TCK-9999")
                .reporterEmail("alice@company.com")
                .title("DB connection pool exhausted")
                .description("All connections taken during peak load")
                .priority("HIGH")
                .slaMinutes(120)
                .source("WEBHOOK")
                .customerVisible(true)
                .addTag("DB")
                .addTag("PERF")
                .build();
        System.out.println("\nManual build: " + detailed);
    }
}
