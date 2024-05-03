package com.example.firstweek;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * Describes a sampling job with successive notes.
 */
public class Job {
    /**
     * Interval between notes in semitones.
     */
    public enum Interval {
        ONE(1),
        THREE(3),
        SIX(6),
        TWELVE(12);

        private final int value;

        private Interval(int value) {
            this.value = value;
        }

        /**
         * Gets the ordinal value of this enum instance.
         */
        public int getValue() {
            return value;
        }
    }

    // Internal count of instances created.
    // Used to construct the default name for the job.
    private static int count = 1;

    public Job() {
        this("Job" + Job.count);
    }

    /**
     * Constructs a job with default values.
     */
    public Job(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.fromNote = 40;
        this.toNote = 120;
        this.interval = Interval.SIX;

        // All note duration values are in milliseconds:
        this.noteDuration = 1000;
        this.noteDecay = 500;
        this.noteGap = 100;

        Job.count++; // increase the internal counter
    }

    /**
     * Sets the name of the job.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the job.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the unique identifier of the job.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Gets the first note of the range.
     */
    public int getFromNote() {
        return this.fromNote;
    }

    /**
     * Sets the first note of the range.
     */
    public void setFromNote(int note) {
        if (note < 0 || note > 127) {
            throw new IllegalArgumentException("Note must be 0...127");
        }
        this.fromNote = note;
    }

    /**
     * Gets the last note of the range.
     */
    public int getToNote() {
        return this.toNote;
    }

    /**
     * Sets the last note of the range. 
     */
    public void setToNote(int note) {
        if (note < 0 || note > 127) {
            throw new IllegalArgumentException("Note must be 0...127");
        }
        this.toNote = note;
    }

    /**
     * Gets the interval between the notes.
     */
    public Interval getInterval() {
        return this.interval;
    }

    /**
     * Sets the interval between the notes.
     */
    public void setInterval(Interval i) {
        this.interval = i;
    }

    /**
     * Gets the note duration.
     *
     * @return the note duration in milliseconds
     */
    public int getNoteDuration() {
        return this.noteDuration;
    }

    /**
     * Sets the duration of each note.
     *
     * @param duration note duration (milliseconds)
     */
    public void setNoteDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Note duration must be positive");
        }

        this.noteDuration = duration;
    }

    /**
     * Gets the note decay time.
     *
     * @return the note decay time in milliseconds
     */
    public int getNoteDecay() {
        return this.noteDecay;
    }

    /**
     * Sets the decay of each note.
     *
     * @param decay note decay (milliseconds)
     */
    public void setNoteDecay(int decay) {
        if (decay <= 0) {
            throw new IllegalArgumentException("Note decay time must be positive");
        }

        this.noteDecay = decay;
    }

    /**
     * Gets the gap time between notes.
     *
     * @return the note gap time in milliseconds
     */
    public int getNoteGap() {
        return this.noteGap;
    }

    /**
     * Sets the gap time between notes.
     *
     * @param gap note gap time (milliseconds)
     */
    public void setNoteGap(int gap) {
        if (gap <= 0) {
            throw new IllegalArgumentException("Note gap time must be positive");
        }

        this.noteGap = gap;
    }

    public List<Integer> getNotes() {
        List<Integer> notes = new ArrayList<>();

        int note = this.fromNote;
        while (note <= this.toNote) {
            notes.add(note);
            note += this.interval.getValue();
        }

        return notes;
    }

    /**
     * Gets a string representation of the job.
     */
    @Override
    public String toString() {
        return String.format(
                "%s: from %d to %d by %d semitones, duration %d ms, decay %d ms, gap %d ms",
                this.getName(),
                this.getFromNote(),
                this.getToNote(),
                this.interval.getValue(),
                this.getNoteDuration(),
                this.getNoteDecay(),
                this.getNoteGap()
        );
    }

    //
    // Private fields
    //

    private String name;
    private UUID id;
    private int fromNote;
    private int toNote;
    private Interval interval;
    private int noteDuration;  // milliseconds
    private int noteDecay;  // note decay time in ms
    private int noteGap;  // note gap time in ms
}