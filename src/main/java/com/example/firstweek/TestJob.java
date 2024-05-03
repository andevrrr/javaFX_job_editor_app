package com.example.firstweek;

public class TestJob {
    public static void main(String[] args) {
        Job job = new Job("Test");
        job.setFromNote(60);
        job.setToNote(92);
        System.out.println(job);
        System.out.println(job.getId());

        var notes = job.getNotes();
        for (var note : notes) {
            System.out.print(note);
            System.out.print(", ");
        }
        System.out.println();
    }
}
