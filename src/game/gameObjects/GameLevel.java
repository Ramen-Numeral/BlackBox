package game.PlayEvents;

import game.api.APICalls;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.io.Serial;
import java.util.List;

public final class GameLevel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // --- Final Fields (State of the Level) ---
    final private byte[] commandPromptAudio;
    final private String command;
    final private String txtPath;
    final private double[] embedding;
    final private byte[] narrationAudio;
    final private ArrayList<String> availableCommands;
    private boolean played;


    public GameLevel(String txtPath) throws IOException {
        HashMap<String, String> vals = txtParse(txtPath);

        // 1. Initialize List field and populate it
        this.availableCommands = new ArrayList<>();
        String options = vals.get("available_commands");
        if (options != null) {
            // Split using the escaped dollar sign to treat it as a literal delimiter
            String[] commands = options.trim().split("\\$");
            availableCommands.addAll(Arrays.asList(commands));
        }

        // 2. Initialize simple final fields
        this.command = vals.get("command");
        this.txtPath = txtPath;

        // 3. Initialize audio final fields
        this.narrationAudio = APICalls.textToSpeech(vals.get("speech_text"));
        this.commandPromptAudio = APICalls.textToSpeech(vals.get("command_prompt"));

        // 4. Initialize embedding to compare to user input
        this.embedding = APICalls.getEmbeddedVal(this.command);

        // 5. Initialize mutable and static fields
        this.played = false;

    }

    public HashMap<String, String> txtParse(String txtpath){
        String localPath = txtpath.trim().toLowerCase();
        HashMap<String, String> label2val = new HashMap<>();

        if(!localPath.endsWith(".txt")){
            localPath += ".txt";
        }

        File txt = new File(localPath);
        String line;

        try(BufferedReader buff = new BufferedReader(new FileReader(txt))){
            while((line = buff.readLine())!=null){
                String[] vals = line.split(":");

                if(vals.length==2) {
                    // Store the key/value pair
                    label2val.put(vals[0].trim().toLowerCase(),
                            vals[1].trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to open or read txt file: " + localPath);
            throw new RuntimeException("Error during file parsing: " + localPath, e);
        }
        return label2val;
    }


    public double getEmbeddingNorm() {
        if (embedding == null) return 0.0;
        double sum = 0.0;
        for (double v : embedding) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    // ------------------------------------
    // --- GETTERS AND SETTERS ---
    // ------------------------------------

    // --- Final Field Getters (Immutability maintained with copies) ---

    public String getCommand() {
        return command;
    }

    public String getTxtPath() {
        return txtPath;
    }

    // Reworked getter for the new commandPromptAudio field
    public byte[] getCommandPromptAudio() {
        return commandPromptAudio != null ? Arrays.copyOf(commandPromptAudio, commandPromptAudio.length) : null;
    }

    // Reworked getter for the new narrationAudio field
    public byte[] getNarrationAudio() {
        return narrationAudio != null ? Arrays.copyOf(narrationAudio, narrationAudio.length) : null;
    }

    public double[] getEmbedding() {
        return embedding != null ? Arrays.copyOf(embedding, embedding.length) : null;
    }

    // Reworked getter for the final ArrayList field
    // Returns a copy of the list (List<String>) to prevent external modification of the internal list reference,
    // though the strings inside are immutable anyway.
    public List<String> getAvailableCommands() {
        return new ArrayList<>(availableCommands);
    }

    // --- Mutable Field Getter and Setter ---

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }


    // ------------------------------------
    // --- OBJECT OVERRIDES ---
    // ------------------------------------

    @Override
    public String toString() {
        return "GameLevel{" +
                "command='" + command + '\'' +
                ", txtPath='" + txtPath + '\'' +
                ", played=" + played +
                ", currentLevel=" + level +
                ", embeddingSize=" + (embedding != null ? embedding.length : 0) +
                ", narrationAudioSize=" + (narrationAudio != null ? narrationAudio.length : 0) +
                ", commandPromptAudioSize=" + (commandPromptAudio != null ? commandPromptAudio.length : 0) +
                ", availableCommands=" + availableCommands +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameLevel that = (GameLevel) o;

        // Compare all final fields for equality
        return Objects.equals(command, that.command) &&
                Objects.equals(txtPath, that.txtPath) &&
                Objects.equals(availableCommands, that.availableCommands) &&
                Arrays.equals(commandPromptAudio, that.commandPromptAudio) &&
                Arrays.equals(narrationAudio, that.narrationAudio) &&
                Arrays.equals(embedding, that.embedding);
    }

    @Override
    public int hashCode() {
        // Hash all final fields
        int result = Objects.hash(command, txtPath, availableCommands);
        result = 31 * result + Arrays.hashCode(commandPromptAudio);
        result = 31 * result + Arrays.hashCode(narrationAudio);
        result = 31 * result + Arrays.hashCode(embedding);
        return result;
    }
}