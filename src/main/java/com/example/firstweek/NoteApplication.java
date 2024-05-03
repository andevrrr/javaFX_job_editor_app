package com.example.firstweek;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NoteApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Job job = new Job("Initial Job Name");

        TableView<Note> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Note, Integer> numberColumn = new TableColumn<>("Note");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Note, Integer> velocityColumn = new TableColumn<>("Velocity");
        velocityColumn.setCellValueFactory(new PropertyValueFactory<>("velocity"));

        TableColumn<Note, Integer> startTimeColumn = new TableColumn<>("Start (ms)");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Note, Integer> endTimeColumn = new TableColumn<>("End (ms)");
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        tableView.getColumns().add(numberColumn);
        tableView.getColumns().add(velocityColumn);
        tableView.getColumns().add(startTimeColumn);
        tableView.getColumns().add(endTimeColumn);

        // Populate the TableView
        ObservableList<Note> notes = FXCollections.observableArrayList(generateNotes(job));
        tableView.setItems(notes);

        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox, 600, 400 );

        primaryStage.setTitle("Job Note Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Note> generateNotes(Job job) {
        List<Note> notes = new ArrayList<>();
        List<Integer> noteValues = job.getNotes();
        int currentTime = 0;

        for (Integer note : noteValues) {
            for (int velocity = 1; velocity <= 127; velocity++) {
                int startTime = currentTime;
                int endTime = startTime + job.getNoteDuration() + job.getNoteDecay();
                notes.add(new Note(note, velocity, startTime, endTime));
                currentTime = endTime + job.getNoteGap();
            }
        }
        return notes;
    }
}
