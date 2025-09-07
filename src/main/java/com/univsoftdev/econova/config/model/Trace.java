package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.contabilidad.SubSystem;
import com.univsoftdev.econova.core.model.AuditBaseModel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Entity class representing system traces or audit logs.
 *
 * This class extends BaseModel to inherit comprehensive auditing features
 * including: - Automatic timestamp management (creation and modification dates)
 * - User audit tracking (who created/modified the trace) - Tenant isolation for
 * multitenancy support - Soft delete capability - Optimistic concurrency
 * control
 *
 * Traces are used to record system events, user actions, or application
 * activities for debugging, monitoring, and audit purposes. Each trace captures
 * a specific event with its timestamp and descriptive information.
 *
 * @entity Indicates this class is a JPA entity
 * @table Maps to the "sys_traces" database table
 * @extends BaseModel inherits auditing and multitenancy features
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sys_traces", schema = "security")
public class Trace extends AuditBaseModel {

    private String host;
    /**
     * Detailed description of the traced event or activity. Contains contextual
     * information about what happened, including: - Event type or category -
     * Relevant parameters or data - Outcome or result of the event - Any error
     * messages or status information
     *
     * This field should provide sufficient context for debugging and audit
     * analysis.
     */
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SubSystem subSystem;

    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    /**
     * Gets the descriptive information about the traced event.
     *
     * @return String containing the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the descriptive information about the traced event.
     *
     * @param description the detailed description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public SubSystem getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(SubSystem subSystem) {
        this.subSystem = subSystem;
    }

}
