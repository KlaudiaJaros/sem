FROM openjdk:latest
COPY ./target/sem_repo-0.1.0.1-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "sem_repo-0.1.0.1-jar-with-dependencies.jar"]