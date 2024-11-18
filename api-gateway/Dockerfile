FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR /application
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dmaven.test.skip

FROM eclipse-temurin:21.0.3_9-jre
VOLUME /tmp
RUN useradd -ms /bin/bash spring-user
USER spring-user

COPY --from=builder /application/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
