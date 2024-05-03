package com.example.firstweek;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class JobApplication extends Application {

    private Job job;
    private Label durationLabel, decayLabel, gapLabel;
    private Canvas timingCanvas;

    @Override
    public void start(Stage primaryStage) {
        job = new Job("Initial Job Name");

        Label nameLabel = new Label("Job Name:");
        TextField nameField = new TextField();

        Label startLabel = new Label("Start note:");
        Spinner<Integer> startSpinner = new Spinner<>(0, 127, job.getFromNote());
        startSpinner.setEditable(true); //Setting the spinner editable
        startSpinner.setPrefSize(75, 25); //Setting the size

        Label endLabel = new Label("End note:");
        Spinner<Integer> endSpinner = new Spinner<>(0, 127, job.getToNote());
        endSpinner.setEditable(true);
        endSpinner.setPrefSize(75, 25);

        Button updateButton = new Button("Update");

        //nameField.setOnAction(e -> updateJobName(nameField)); // Updates the job name on enter

        // Updates the job name on click
        updateButton.setOnAction(e -> {
            updateJobDetails(nameField, startSpinner, endSpinner);
        });

        // Radio Buttons for Interval Selection
        ToggleGroup intervalGroup = new ToggleGroup();

        RadioButton rbOne = new RadioButton("1 Semitone");
        rbOne.setUserData(Job.Interval.ONE);
        rbOne.setToggleGroup(intervalGroup);

        RadioButton rbThree = new RadioButton("3 Semitones");
        rbThree.setUserData(Job.Interval.THREE);
        rbThree.setToggleGroup(intervalGroup);

        RadioButton rbSix = new RadioButton("6 Semitones");
        rbSix.setUserData(Job.Interval.SIX);
        rbSix.setToggleGroup(intervalGroup);

        RadioButton rbTwelve = new RadioButton("12 Semitones");
        rbTwelve.setUserData(Job.Interval.TWELVE);
        rbTwelve.setToggleGroup(intervalGroup);

        // Setting the default selected radio button based on the current job interval
        switch (job.getInterval()) {
            case ONE:
                rbOne.setSelected(true);
                break;
            case THREE:
                rbThree.setSelected(true);
                break;
            case SIX:
                rbSix.setSelected(true);
                break;
            case TWELVE:
                rbTwelve.setSelected(true);
                break;
        }

        VBox radioBox = new VBox(10, rbOne, rbThree, rbSix, rbTwelve);
        TitledPane intervalPane = new TitledPane("Select Interval", radioBox);
        intervalPane.setCollapsible(false);

        intervalGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Job.Interval selectedInterval = (Job.Interval) newValue.getUserData();
                job.setInterval(selectedInterval);
                System.out.println(job);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(startLabel, 0, 1);
        gridPane.add(startSpinner, 1, 1);
        gridPane.add(endLabel, 0, 2);
        gridPane.add(endSpinner, 1, 2);
        gridPane.add(updateButton, 0, 3, 2, 1);
        GridPane.setHalignment(updateButton, HPos.CENTER);
        gridPane.add(intervalPane, 0, 4, 2, 1);

        Slider durationSlider = setupSlider(100, 5000, job.getNoteDuration(), "Duration: %d ms");
        Slider decaySlider = setupSlider(100, 4500, job.getNoteDecay(), "Decay: %d ms");
        Slider gapSlider = setupSlider(100, 500, job.getNoteGap(), "Gap: %d ms");

        timingCanvas = new Canvas(300, 50);
        updateCanvas();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(gridPane, durationSlider, durationLabel, decaySlider, decayLabel, gapSlider, gapLabel, timingCanvas);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setTitle("Job Update Form");
        primaryStage.setScene(scene);
        primaryStage.show();
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
                job.setNoteDuration(newValue.intValue());
            } else if (labelTextFormat.contains("Decay")) {
                job.setNoteDecay(newValue.intValue());
            } else if (labelTextFormat.contains("Gap")) {
                job.setNoteGap(newValue.intValue());
            }
            updateCanvas();
        });

        return slider;
    }

    private void updateCanvas() {
        GraphicsContext gc = timingCanvas.getGraphicsContext2D();
        double width = timingCanvas.getWidth();
        double total = job.getNoteDuration() + job.getNoteDecay() + job.getNoteGap();
        double durationWidth = width * job.getNoteDuration() / total;
        double decayWidth = width * job.getNoteDecay() / total;
        double gapWidth = width * job.getNoteGap() / total;

        gc.clearRect(0, 0, width, timingCanvas.getHeight());
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 10, durationWidth, 30);
        gc.setFill(Color.RED);
        gc.fillRect(durationWidth, 10, decayWidth, 30);
        gc.setFill(Color.BLUE);
        gc.fillRect(durationWidth + decayWidth, 10, gapWidth, 30);
    }

    private void updateJobDetails(TextField nameField, Spinner<Integer> startSpinner, Spinner<Integer> endSpinner) {
        try {
            // Updating the job name
            String newName = nameField.getText().trim();
            job.setName(newName);

            // Updating the start and end notes
            int startNote = startSpinner.getValue();
            int endNote = endSpinner.getValue();

            job.setFromNote(startNote);
            job.setToNote(endNote);

            System.out.println(job);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateJobName(TextField nameField) {
        try {
            String newName = nameField.getText().trim();
            job.setName(newName);
            System.out.println(job);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
