package game.gameUtil.objs;

import game.api.APICalls;
import game.gameUtil.helpers.LevelUtil;

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
    private final String narrationTxt;
    private boolean played;

    public GameLevel(String txtPath) throws IOException {
        HashMap<String, String> vals = LevelUtil.parseLvlTxt(txtPath);
        System.out.println("Parsed level file contents: " + vals);
        this.availableCommands = LevelUtil.createAvailableCommand(vals);
        this.command = vals.get("command");
        this.txtPath = txtPath;
        this.narrationTxt = vals.get("speech text");

        // Get Polly audio
        byte[] rawNarration = APICalls.callPolly(vals.get("speech text"));
        byte[] rawCommandPrompt = APICalls.callPolly(vals.get("command"));

        // Pad with 1 second silence front and back
        this.narrationAudio = addSilencePadding(rawNarration, 16000, 2, 1); // sampleRate=16kHz, bytesPerSample=2, seconds=1
        this.commandPromptAudio = addSilencePadding(rawCommandPrompt, 16000, 2, 1);
        this.embedding = APICalls.callEmbeddings(this.command);
        this.played = false;
    }

    public boolean isAvailableCommand(String com){return availableCommands.contains(com);}


    public String getNarrationText(){return narrationTxt;}

    /** Add silence at beginning and end */
    private byte[] addSilencePadding(byte[] audio, int sampleRate, int bytesPerSample, int seconds) {
        int silenceLength = sampleRate * bytesPerSample * seconds; // mono channel
        byte[] padded = new byte[audio.length + silenceLength * 2]; // front + back
        // front silence already zeroed
        System.arraycopy(audio, 0, padded, silenceLength, audio.length);
        // back silence automatically zeroed
        return padded;
    }

    // --- Getters ---
    public String getCommand() { return command; }
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
