Running the Docker Build
Make sure you are inside a directory that contains a .env.secrets file before running:

docker run -it \
-v $(pwd):/app \
-w /app \
--name blackbox_run \
blackbox

This program requires valid API credentials. It will not run without:
- An OpenAI API key
- AWS access key and secret
- A properly formatted .env.secrets file in the working directory

Overview
This project is built as an audio command pipeline. The system operates through several tasks:
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

Build Details
Gradle is used for dependency management, including AWS and OpenAI SDKs.
Docker is used for packaging.
