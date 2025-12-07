package game.tasks;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;

public class TextOutTask implements Runnable {

    private final BlockingQueue<String> GUITextQueue;
    private final JTextArea textArea;
    private final long charDelayMs;

    /**
     * @param GUITextQueue The queue containing messages to display
     * @param textArea The JTextArea in the terminal GUI
     * @param charDelayMs Delay in milliseconds per character for typing effect
     */
    public TextOutTask(BlockingQueue<String> GUITextQueue, JTextArea textArea, long charDelayMs) {
        this.GUITextQueue = GUITextQueue;
        this.textArea = textArea;
        this.charDelayMs = charDelayMs;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Wait for the next message
                String message = GUITextQueue.take(); // blocks until a message is available
                if(message.equals("loading..."));
                textArea.setText("");
                // Append each character with typing effect
                for (int i = 0; i < message.length(); i++) {
                    final String toAppend = String.valueOf(message.charAt(i));

                    SwingUtilities.invokeLater(() -> {
                        textArea.append(toAppend);
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    });

                    Thread.sleep(charDelayMs);
                }

                // Add newline after each full message
                SwingUtilities.invokeLater(() -> textArea.append("\n"));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // exit gracefully
        }
    }
}
