FROM openjdk:latest
COPY ./target/sem_repo.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "sem_repo.jar", "db:3306"]