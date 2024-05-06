package com.example.firstweek;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JobApplication extends Application {

    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private Job currentJob;
    private TextField jobNameField;
    private Spinner<Integer> startSpinner , endSpinner ;
    private Label durationLabel, decayLabel, gapLabel;
    private ToggleGroup intervalGroup;
    private TableView<Note> notesTableView;
    private Canvas timingCanvas; // for note duration, decay, and gap
    private Slider durationSlider, decaySlider, gapSlider;

    @Override
    public void start(Stage primaryStage) {
        jobList.addAll(Job.generateSampleJobs());

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 600);
        SplitPane mainSplit = new SplitPane();
        mainSplit.setOrientation(Orientation.HORIZONTAL);
        root.setCenter(mainSplit);

        ListView<Job> jobListView = setupJobListView();
        VBox jobEditor = setupJobEditor();
        notesTableView = setupNotesTable();

        SplitPane rightSplit = new SplitPane();
        rightSplit.setOrientation(Orientation.VERTICAL);
        rightSplit.getItems().addAll(jobEditor, notesTableView);
        rightSplit.setDividerPositions(0.5, 0.8);

        mainSplit.getItems().addAll(jobListView, rightSplit);
        mainSplit.setDividerPositions(0.3);

        primaryStage.setTitle("Job Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ListView<Job> setupJobListView() {
        ListView<Job> listView = new ListView<>(jobList);
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentJob = newSelection;
                updateJobEditor();
                updateNotesTable();
                updateCanvas();
            }
        });
        return listView;
    }

    private VBox setupJobEditor() {
        Label nameLabel = new Label("Job Name:");
        jobNameField = new TextField();
        jobNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentJob != null) {
                currentJob.setName(newVal);
                updateNotesTable();
            }
        });

        int fromNote = (currentJob != null) ? currentJob.getFromNote() : 40;
        int toNote = (currentJob != null) ? currentJob.getToNote() : 80;
        int noteDuration = (currentJob != null) ? currentJob.getNoteDuration() : 1000;

        Label startLabel = new Label("Start note:");
        startSpinner = new Spinner<>(0, 127, fromNote);
        startSpinner.setEditable(true);
        startSpinner.setPrefSize(75, 25);
        startSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (currentJob != null && newValue != null) {
                currentJob.setFromNote(newValue);
                updateNotesTable();
            }
        });

        Label endLabel = new Label("End note:");
        endSpinner = new Spinner<>(0, 127, toNote);
        endSpinner.setEditable(true);
        endSpinner.setPrefSize(75, 25);
        endSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (currentJob != null && newValue != null) {
                currentJob.setToNote(newValue);
                updateNotesTable();
            }
        });

        durationSlider = setupSlider(100, 5000, noteDuration, "Duration: %d ms");
        decaySlider = setupSlider(100, 4500, noteDuration, "Decay: %d ms");
        gapSlider = setupSlider(100, 500, noteDuration, "Gap: %d ms");

        intervalGroup = new ToggleGroup();
        HBox intervalBox = new HBox(10);
        intervalBox.getChildren().addAll(
                createIntervalRadioButton("1 Semitone", Job.Interval.ONE),
                createIntervalRadioButton("3 Semitones", Job.Interval.THREE),
                createIntervalRadioButton("6 Semitones", Job.Interval.SIX),
                createIntervalRadioButton("12 Semitones", Job.Interval.TWELVE)
        );

        timingCanvas = new Canvas(300, 50);
        updateCanvas();

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.addRow(0, nameLabel, jobNameField);
        gridPane.addRow(1, startLabel, startSpinner);
        gridPane.addRow(2, endLabel, endSpinner);
        gridPane.add(intervalBox, 1, 3);
        VBox editorBox = new VBox(10);
        editorBox.setPadding(new Insets(20));
        editorBox.getChildren().addAll(gridPane, durationSlider, decaySlider, gapSlider, timingCanvas);
        return editorBox;
    }

    private Slider setupSlider(int min, int max, int initialValue, String labelTextFormat) {
        Slider slider = new Slider(min, max, initialValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setBlockIncrement((max - min) / 10);

        Label label = new Label(String.format(labelTextFormat, initialValue));
        if (labelTextFormat.contains("Duration")) {
            durationLabel = label;
        } else if (labelTextFormat.contains("Decay")) {
            decayLabel = label;
        } else if (labelTextFormat.contains("Gap")) {
            gapLabel = label;
        }

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(String.format(labelTextFormat, newValue.intValue()));
            if (labelTextFormat.contains("Duration")) {
                currentJob.setNoteDuration(newValue.intValue());
            } else if (labelTextFormat.contains("Decay")) {
                currentJob.setNoteDecay(newValue.intValue());
            } else if (labelTextFormat.contains("Gap")) {
                currentJob.setNoteGap(newValue.intValue());
            }
            updateCanvas();
        });

        return slider;
    }

    private RadioButton createIntervalRadioButton(String text, Job.Interval interval) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setUserData(interval);
        radioButton.setToggleGroup(intervalGroup);
        radioButton.setOnAction(e -> {
            if (currentJob != null) {
                currentJob.setInterval(interval);
                updateCanvas();
                updateNotesTable();
            }
        });
        return radioButton;
    }

    private TableView<Note> setupNotesTable() {
        TableView<Note> tableView = new TableView<>();
        TableColumn<Note, Integer> numberColumn = new TableColumn<>("Note");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn<Note, Integer> velocityColumn = new TableColumn<>("Velocity");
        velocityColumn.setCellValueFactory(new PropertyValueFactory<>("velocity"));
        TableColumn<Note, Integer> startTimeColumn = new TableColumn<>("Start (ms)");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        TableColumn<Note, Integer> endTimeColumn = new TableColumn<>("End (ms)");
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        tableView.getColumns().addAll(numberColumn, velocityColumn, startTimeColumn, endTimeColumn);
        return tableView;
    }

    private void updateJobEditor() {
        if (currentJob == null) return;
        jobNameField.setText(currentJob.getName());
        startSpinner.getValueFactory().setValue(currentJob.getFromNote());
        endSpinner .getValueFactory().setValue(currentJob.getToNote());
        durationSlider.setValue(currentJob.getNoteDuration());
        decaySlider.setValue(currentJob.getNoteDecay());
        gapSlider.setValue(currentJob.getNoteGap());
        intervalGroup.getToggles().forEach(toggle -> {
            if (((Job.Interval)toggle.getUserData()) == currentJob.getInterval()) {
                intervalGroup.selectToggle(toggle);
            }
        });
    }

    private void updateCanvas() {
        if (currentJob == null) return;
        GraphicsContext gc = timingCanvas.getGraphicsContext2D();
        double width = timingCanvas.getWidth();
        double total = currentJob.getNoteDuration() + currentJob.getNoteDecay() + currentJob.getNoteGap();
        double durationWidth = width * currentJob.getNoteDuration() / total;
        double decayWidth = width * currentJob.getNoteDecay() / total;
        double gapWidth = width * currentJob.getNoteGap() / total;

        gc.clearRect(0, 0, width, timingCanvas.getHeight());
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 10, durationWidth, 30);
        gc.setFill(Color.RED);
        gc.fillRect(durationWidth, 10, decayWidth, 30);
        gc.setFill(Color.BLUE);
        gc.fillRect(durationWidth + decayWidth, 10, gapWidth, 30);
    }

    private void updateNotesTable() {
        if (currentJob == null) {
            notesTableView.setItems(FXCollections.observableArrayList());  // Clear table if no job is selected
        } else {
            ObservableList<Note> notes = FXCollections.observableArrayList(currentJob.generateNotes());
            notesTableView.setItems(notes);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
