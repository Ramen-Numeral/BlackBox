Example docker build
inside a directory containing a .env.secrets file
docker run -it \
-v $(pwd):/app \
-w /app \
--name blackbox_run \
blackbox

the program will fail without an open ai key, and aws api credentials .env.secrets