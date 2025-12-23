/////////////////////////////////////
a video demo of play is provided in the root at /demo.MOV
////////////////////////////////////

This program requires: 
- An OpenAI API key
- AWS access key and secret
- A properly formatted .env.secrets file in the working directory (.env.secrets.example provided)

Build Details
Gradle is used for dependency management, including AWS and OpenAI SDKs.

\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

Compilation Instructions:
This project uses the Gradle Wrapper. You do not need to install Gradle. When you run the wrapper commands below, Gradle will automatically create a .gradle directory (if one does not exist) and download the correct Gradle version locally. It does not install Gradle system-wide.

========================================
Running the Project from the Command Line

Build the project:
- Mac/Linux:
- ./gradlew build
- Windows:
- gradlew.bat build

Run the project:
- Mac/Linux:
- ./gradlew run
- Windows:
- gradlew.bat run

All dependencies will be included in the wrapper.

========================================

Running the Project Inside an IDE

Open the project folder in your IDE.
- The IDE should automatically detect that this is a Gradle project.
- When prompted, import or load the project as a Gradle project.
- Allow the IDE to finish its Gradle sync. This will download any needed dependencies.
- After the sync finishes, open the class that contains the main method.
- Use the IDE’s normal Run/Play button to run the Main class.
- You may optionally run "./gradlew build" in the ide terminal before opening the project to ensure dependencies are ready.

=======================================

Successful Compilation:
If the program has successfully compiled, you will see a small interface in the center of the screen asking to enter a decibel threshold. 
After you select your threshold 

- +1-2 decibels above the ambient level should be appropriate. There is tolerance for falsely triggered speech input;
even if speech is falsely detected, the game will recognize
it as an invalid command and ignore it up to the error threshold that prevent infinite speech detection looping (20 errors)
- If the microphone is not picking up on your speech, try restarting the game and lowering the threshold.
- Note that a negative readout is normal. This is the "unity gain" on the input line.

After you select the threshold, the main gui will show and the audio will begin.

If there is no .env.secrets file in the root directory, a file not found exception will be thrown. 
The game will still appear, but any attempted speech input will throw a domino effect of errors.

========================================

Overview
This project is an audio command pipeline. The system operates through several tasks:
1. Ambient Decibel Monitoring:
   A background thread continually measures ambient sound levels. When the user speaks above a defined threshold, it triggers the recorder.
2. Rolling Pre-buffer Recorder:
   Another thread constantly records a rolling audio buffer. When a trigger occurs, this buffer is appended to the new recording to capture audio just before activation.
3. Audio Input Event Processing:
   When recording completes, an Audio Input Event is created. Inside this event, Whisper is used for speech-to-text, embeddings are generated, and the text is matched to a command key.
4. Command Queue:
   The resolved command is pushed to the Command Task Queue. To prevent outdated responses, the queue is cleared each time a new valid command is added.
5. Audio Output Task:
   Commands are forwarded to an audio-out thread. This task places text into the TextOutTask queue so the GUI can display output in real time as the level or sequence plays.
6. Text Output Task:
   Outputs text to the GUI as the audio plays.
The terminal should show output as the game progresses so the thread activity can be traced.
