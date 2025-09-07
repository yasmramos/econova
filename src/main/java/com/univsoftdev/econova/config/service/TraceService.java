package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Trace;
import com.univsoftdev.econova.config.repository.TraceRepository;
import com.univsoftdev.econova.contabilidad.SubSystem;
import com.univsoftdev.econova.core.service.BaseService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class TraceService extends BaseService<Trace, TraceRepository> {

    @Inject
    public TraceService(TraceRepository repository) {
        super(repository);
    }

    /**
     * Creates and saves a new trace with start time
     * @param description Trace description
     * @param host Host or origin of the trace
     * @param subSystem Subsystem where the trace originated
     * @return The created Trace entity with start time set
     */
    public Trace startTrace(String description, String host, SubSystem subSystem) {
        Trace trace = new Trace();
        trace.setDescription(description);
        trace.setHost(host);
        trace.setSubSystem(subSystem);
        trace.setTimeStart(LocalDateTime.now());
        repository.save(trace);
        return trace;
    }

    /**
     * Completes a trace by setting the end time
     * @param trace The trace to complete
     * @return The completed trace
     */
    public Trace completeTrace(Trace trace) {
        trace.setTimeEnd(LocalDateTime.now());
        repository.update(trace);
        return trace;
    }

    /**
     * Creates a complete trace with start and end time (for immediate operations)
     * @param description Trace description
     * @param host Host or origin
     * @param subSystem Subsystem
     * @return The completed trace
     */
    public Trace createCompleteTrace(String description, String host, SubSystem subSystem) {
        Trace trace = startTrace(description, host, subSystem);
        return completeTrace(trace);
    }

    /**
     * Finds traces by subsystem
     * @param subSystem The subsystem to filter by
     * @return List of traces for the specified subsystem
     */
    public List<Trace> findBySubSystem(SubSystem subSystem) {
        return repository.findBySubSystem(subSystem);
    }

    /**
     * Finds traces by host
     * @param host The host to filter by
     * @return List of traces from the specified host
     */
    public List<Trace> findByHost(String host) {
        return repository.findByHost(host);
    }

    /**
     * Finds traces within a time range
     * @param start Start time
     * @param end End time
     * @return List of traces within the specified time range
     */
    public List<Trace> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimeStartBetween(start, end);
    }

    /**
     * Finds incomplete traces (without end time)
     * @return List of traces that haven't been completed
     */
    public List<Trace> findIncompleteTraces() {
        return repository.findByTimeEndIsNull();
    }

    /**
     * Calculates duration of a trace in milliseconds
     * @param trace The trace to calculate duration for
     * @return Duration in milliseconds, or null if not completed
     */
    public Long calculateDuration(Trace trace) {
        if (trace.getTimeStart() != null && trace.getTimeEnd() != null) {
            java.time.Duration duration = java.time.Duration.between(
                trace.getTimeStart(), trace.getTimeEnd()
            );
            return duration.toMillis();
        }
        return null;
    }
}