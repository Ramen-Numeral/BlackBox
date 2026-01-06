# Audio-Only Accessible Game
**An immersive audio-command pipeline designed for visually impaired players.**

This project implements a multithreaded system that handles real-time ambient monitoring, rolling audio buffers, vector similarity, and AI-driven speech-to-text processing to create a gaming experience.
A GUI experience is available, but not necessary for game play.

## Technical Specifications

### Core Engine & UI
* **Language:** Java (JDK 8+)
* **GUI Framework:** **Java Swing** 
* **Build Tool:** **Gradle** 

### Audio Pipeline (Java Sound API)
* **Input/Output:** **Java Sound API** via `javax.sound.sampled` to interface directly with system hardware for low-latency PCM audio capture and playback.
* **Concurrency:** Implemented via the standard Java **Thread and Runnable interfaces** to manage non-blocking, simultaneous audio monitoring and processing.

### AI & Cloud Integration (Official SDKs)
* **Speech-to-Text:** **OpenAI Whisper** for user input resolution via the OpenAI Java SDK.
* **Command Logic:** **OpenAI Embeddings** for semantic vector comparison via the OpenAI Java SDK.
* **Narrative Voice:** **Amazon Polly** via the AWS Java SDK to generate system speech.

---

## Prerequisites
Before running the application, ensure you have the following credentials and files:
* **OpenAI API Key:** Required for Whisper Speech-to-Text (STT) and text embeddings.
* **AWS Credentials:** Access Key and Secret required for AWS Polly (TTS) integration.
* **Environment Configuration:** A `.env.secrets` file must be present in the working directory. Refer to `.env.secrets.example` for the required schema.

> [!CAUTION]
> If the `.env.secrets` file is missing, the program will throw a `FileNotFoundException`. While the GUI may still load, any speech input will trigger a cascade of errors as the API calls fail.

---

##  Build & Compilation
This project utilizes the **Gradle Wrapper** to handle dependencies. No library installations are required.

### Command Line Instructions
**1. Build the project:**
* **Mac/Linux:** `./gradlew build`
* **Windows:** `gradlew.bat build`

**2. Run the project:**
* **Mac/Linux:** `./gradlew run`
* **Windows:** `gradlew.bat run`

### IDE Integration
1. Open the project folder in your IDE (IntelliJ IDEA or Eclipse).
2. Import/Load the project as a **Gradle project** when prompted.
3. Allow the IDE to complete the **Gradle Sync** to download SDKs and dependencies.
4. Locate the `Main` class and use the IDE's **Run** button.

---

## System Calibration
Upon startup, a calibration interface will appear to configure the audio input sensitivity.


### Decibel Thresholding
1. **Selection:** Choose a threshold **+3-5 decibels** above the ambient room noise.
2. **Unity Gain:** Note that negative readouts are normal; this represents the "unity gain" on the input line.
3. **Input Tolerance:** The system includes logic to ignore false triggers and unmatched commands. To prevent infinite loops, the system will automatically ignore invalid detection after 20 consecutive errors.

---
## Game Play

>As an audio-first experience, the narrative-progression relies entirely on voice commands and audio feedback.

### Basic Controls
The system matches your speech against vectorized embeddings to trigger game actions.
* **Navigation:** When player input is necessary to progress the game, the system will prompt the user with the available commands and wait for input to continue.
* **Interaction:** The user will speak their command out loud. For example: "Open door," "Pick up item," or "Search room."
* **Exit & Restart:** Should the user want to start a new game or exit the system, they can say "Exit" or "Start a New Game" at any point in time.

---

## Technical Architecture
The core of this project is a high-concurrency audio pipeline managed across several dedicated threads:

### 1. Ambient Monitoring
A background thread continuously samples sound levels. When input crosses the calibrated threshold, it signals the recording state to begin.

### 2. Rolling Pre-buffer Recorder
To prevent "clipping" at the start of user speech, a thread maintains a constant rolling buffer. When triggered, this buffer is prepended to the new recording to ensure the full command is captured.

### 3. Audio Input Event Processing
Once a recording is complete, an AIE is dispatched for analysis:
* **OpenAI Whisper:** Converts the audio waveform to text.
* **Vectorized Embeddings:** Compares the text against valid command keys using vector similarity.


### 4. Command & Task Queuing
* **Command Queue:** Validated commands are pushed to a task queue. The queue is cleared with every new valid command to prioritize the most recent user intent.
* **Audio/Text Sync:** The **Audio Output Task** (AWS Polly) and **Text Output Task** (GUI) run in parallel, ensuring the screen reader and visual display remain synchronized.

--- 

## Technical Challenges & Solutions

### The "Infinite Loop" Problem
**Challenge:** Background noise or AWS/OpenAI latency occasionally triggered the recording state without a valid user command, creating an infinite loop of API calls.
**Solution:** Implemented an **Error Threshold Counter**. The system tracks sequential "Null" or "Invalid" command resolutions; upon reaching 20 errors, the pipeline resets.

### Eliminating Command "Clipping"
**Challenge:** Standard voice triggers often lose the first 200-500ms of audio, causing Whisper to fail on short commands like "Go."
**Solution:** Developed a **Rolling Pre-buffer**. By maintaining a continuous 1-second audio stream in memory, the system "rewinds" slightly upon trigger detection, ensuring the full phoneme sequence is captured for analysis.

---

### Terminal Trace
The terminal provides real-time logs of thread activity, allowing developers to trace the lifecycle of an audio event from "Ambient Trigger," to "API Response," to "Command Execution."