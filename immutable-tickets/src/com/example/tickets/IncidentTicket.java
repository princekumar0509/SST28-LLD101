package com.example.tickets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable incident ticket.
 *
 * Objects are created exclusively through {@link Builder}. Once built:
 *  - all fields are final and private
 *  - there are no setters
 *  - getTags() returns an unmodifiable view, so callers cannot alter internal state
 *
 * To "update" a ticket create a new one via {@link #toBuilder()}.
 */
public final class IncidentTicket {

    // Required fields
    private final String id;
    private final String reporterEmail;
    private final String title;

    // Optional fields
    private final String description;
    private final String priority;        // LOW | MEDIUM | HIGH | CRITICAL
    private final List<String> tags;      // defensive copy, returned as unmodifiable
    private final String assigneeEmail;
    private final boolean customerVisible;
    private final Integer slaMinutes;     // null means "not set"
    private final String source;          // e.g. CLI, WEBHOOK, EMAIL

    // Private: only Builder may call this
    private IncidentTicket(Builder b) {
        this.id              = b.id;
        this.reporterEmail   = b.reporterEmail;
        this.title           = b.title;
        this.description     = b.description;
        this.priority        = b.priority;
        // Defensive copy so mutations to the builder list don't affect us
        this.tags            = Collections.unmodifiableList(new ArrayList<>(b.tags));
        this.assigneeEmail   = b.assigneeEmail;
        this.customerVisible = b.customerVisible;
        this.slaMinutes      = b.slaMinutes;
        this.source          = b.source;
    }

    // --------------------------------------------------------------------- //
    // Getters – no setters exist
    // --------------------------------------------------------------------- //

    public String getId()             { return id; }
    public String getReporterEmail()  { return reporterEmail; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public String getPriority()       { return priority; }
    public String getAssigneeEmail()  { return assigneeEmail; }
    public boolean isCustomerVisible(){ return customerVisible; }
    public Integer getSlaMinutes()    { return slaMinutes; }
    public String getSource()         { return source; }

    /**
     * Returns an *unmodifiable* view of the tags list.
     * Callers cannot add/remove entries through this reference.
     */
    public List<String> getTags() {
        return tags; // already wrapped in Collections.unmodifiableList at construction
    }

    // --------------------------------------------------------------------- //
    // Convenience: rebuild from existing ticket (for "update" scenarios)
    // --------------------------------------------------------------------- //

    /** Returns a Builder pre-populated with this ticket's values. */
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "IncidentTicket{" +
               "id='" + id + '\'' +
               ", reporterEmail='" + reporterEmail + '\'' +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", priority='" + priority + '\'' +
               ", tags=" + tags +
               ", assigneeEmail='" + assigneeEmail + '\'' +
               ", customerVisible=" + customerVisible +
               ", slaMinutes=" + slaMinutes +
               ", source='" + source + '\'' +
               '}';
    }

    // ===================================================================== //
    //  Builder
    // ===================================================================== //

    public static final class Builder {

        // Required
        private String id;
        private String reporterEmail;
        private String title;

        // Optional – sensible defaults
        private String description;
        private String priority;
        private List<String> tags = new ArrayList<>();
        private String assigneeEmail;
        private boolean customerVisible = false;
        private Integer slaMinutes;
        private String source;

        /** Start a blank builder. */
        public Builder() {}

        /** Copy-constructor used by toBuilder() and Builder.from(). */
        private Builder(IncidentTicket t) {
            this.id              = t.id;
            this.reporterEmail   = t.reporterEmail;
            this.title           = t.title;
            this.description     = t.description;
            this.priority        = t.priority;
            this.tags            = new ArrayList<>(t.tags); // mutable copy for the builder
            this.assigneeEmail   = t.assigneeEmail;
            this.customerVisible = t.customerVisible;
            this.slaMinutes      = t.slaMinutes;
            this.source          = t.source;
        }

        /** Factory alternative to toBuilder() for copy-and-modify pattern. */
        public static Builder from(IncidentTicket existing) {
            return new Builder(existing);
        }

        // Fluent setters

        public Builder id(String id)                       { this.id = id;                         return this; }
        public Builder reporterEmail(String email)         { this.reporterEmail = email;            return this; }
        public Builder title(String title)                 { this.title = title;                    return this; }
        public Builder description(String description)     { this.description = description;        return this; }
        public Builder priority(String priority)           { this.priority = priority;              return this; }
        public Builder tags(List<String> tags)             { this.tags = new ArrayList<>(tags);     return this; }
        public Builder addTag(String tag)                  { this.tags.add(tag);                    return this; }
        public Builder assigneeEmail(String email)         { this.assigneeEmail = email;            return this; }
        public Builder customerVisible(boolean visible)    { this.customerVisible = visible;        return this; }
        public Builder slaMinutes(Integer minutes)         { this.slaMinutes = minutes;             return this; }
        public Builder source(String source)               { this.source = source;                  return this; }

        /**
         * Validates all fields and returns an immutable {@link IncidentTicket}.
         *
         * <p>All validation lives here – nowhere else in the codebase.
         */
        public IncidentTicket build() {
            // --- Required fields ---
            Validation.requireTicketId(id);
            Validation.requireEmail(reporterEmail, "reporterEmail");
            Validation.requireNonBlank(title, "title");
            Validation.requireMaxLen(title, 80, "title");

            // --- Optional with constraints ---
            if (assigneeEmail != null) {
                Validation.requireEmail(assigneeEmail, "assigneeEmail");
            }
            Validation.requireOneOf(priority, "priority",
                    "LOW", "MEDIUM", "HIGH", "CRITICAL");
            Validation.requireRange(slaMinutes, 5, 7200, "slaMinutes");

            return new IncidentTicket(this);
        }
    }
}
