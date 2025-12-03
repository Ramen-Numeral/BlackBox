package game.gameObjects;

import game.api.APICalls;
import java.io.*;
import java.util.*;
import java.io.Serial;

public final class GameLevel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // --- Final Fields ---
    private final byte[] commandPromptAudio;
    private final String command;
    private final String txtPath;
    private final double[] embedding;
    private final byte[] narrationAudio;
    private final ArrayList<String> availableCommands;
    private boolean played;

    public GameLevel(String txtPath) throws IOException {
        HashMap<String, String> vals = GameUtil.parseLvlTxt(txtPath);
        this.availableCommands = GameUtil.createAvailableCommand(vals);
        this.command = vals.get("command");
        this.txtPath = txtPath;
        this.narrationAudio = APICalls.textToSpeech(vals.get("speech_text"));
        this.commandPromptAudio = APICalls.textToSpeech(vals.get("command_prompt"));
        this.embedding = APICalls.getEmbeddedVal(this.command);
        this.played = false;
    }

    // --- Getters ---
    public String getCommand() { return command; }
    public String getTxtPath() { return txtPath; }
    public byte[] getCommandPromptAudio() { return commandPromptAudio != null ? Arrays.copyOf(commandPromptAudio, commandPromptAudio.length) : null; }
    public byte[] getNarrationAudio() { return narrationAudio != null ? Arrays.copyOf(narrationAudio, narrationAudio.length) : null; }
    public double[] getEmbedding() { return embedding != null ? Arrays.copyOf(embedding, embedding.length) : null; }
    public List<String> getAvailableCommands() { return new ArrayList<>(availableCommands); }
    public boolean isPlayed() { return played; }
    public void setPlayed(boolean played) { this.played = played; }

    @Override
    public String toString() {
        return "GameLevel{" +
                "command='" + command + '\'' +
                ", txtPath='" + txtPath + '\'' +
                ", played=" + played +
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
        return Objects.equals(command, that.command) &&
                Objects.equals(txtPath, that.txtPath) &&
                Objects.equals(availableCommands, that.availableCommands) &&
                Arrays.equals(commandPromptAudio, that.commandPromptAudio) &&
                Arrays.equals(narrationAudio, that.narrationAudio) &&
                Arrays.equals(embedding, that.embedding);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(command, txtPath, availableCommands);
        result = 31 * result + Arrays.hashCode(commandPromptAudio);
        result = 31 * result + Arrays.hashCode(narrationAudio);
        result = 31 * result + Arrays.hashCode(embedding);
        return result;
    }
}
