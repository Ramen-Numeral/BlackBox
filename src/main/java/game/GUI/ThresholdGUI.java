package game.GUI;

import game.tasks.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

//small gui to start game that accepts the users desired threshold for speech detection
public class ThresholdGUI extends JFrame {

    private final JTextField thresholdField;
    private final JLabel ambientLabel;
    private final JButton startButton;
    private final CompletableFuture<Double> thresholdFuture;

    private final AmbientDBTask ambientTask;
    private final Thread ambientThread;
    private volatile boolean running = true;

    public ThresholdGUI(CompletableFuture<Double> thresholdFuture) {
        this.thresholdFuture = thresholdFuture;

        setTitle("Speech Detection Setup");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JLabel instructionLabel = new JLabel(
                "<html><div style='text-align:center;'>Please select a threshold above which user speech will be detected</div></html>",
                JLabel.CENTER);
        add(instructionLabel);

        ambientLabel = new JLabel("Current ambient dB: calculating...", JLabel.CENTER);
        add(ambientLabel);

        thresholdField = new JTextField("5");
        thresholdField.setHorizontalAlignment(JTextField.CENTER);
        add(thresholdField);

        startButton = new JButton("Submit Threshold");
        startButton.setMargin(new Insets(5, 20, 5, 20));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startButton);
        add(buttonPanel);

        // Start ambient listener in its own thread
        ambientTask = new AmbientDBTask();
        ambientThread = new Thread(ambientTask, "AmbientDBThread");
        ambientThread.start();

        // Timer to update ambient label
        Timer timer = new Timer(100, e -> {
            if (!running) return;
            try {
                double currentDB = ambientTask.getDB();
                ambientLabel.setText(String.format("Current ambient dB: %.2f", currentDB));
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });
        timer.start();

        startButton.addActionListener(e -> {
            double value;
            try {
                value = Double.parseDouble(thresholdField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number!");
                return;
            }
            running = false;
            ambientThread.interrupt();
            timer.stop();
            dispose();
            thresholdFuture.complete(value);
        });

        // Make sure GUI shows
        SwingUtilities.invokeLater(() -> {
            pack(); // optional: resize frame to fit components neatly
            setLocationRelativeTo(null); // center the window
            setVisible(true); // make it appearsetVisible(true));
        });


    }

}
