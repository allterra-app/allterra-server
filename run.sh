./gradlew clean &&
./gradlew build &&
docker build --tag=allterra:latest . &&
docker compose up