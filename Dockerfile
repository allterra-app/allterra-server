FROM eclipse-temurin:21
MAINTAINER ALLTERRA
COPY build/libs/allterra-*-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","-jar","/application.jar"]