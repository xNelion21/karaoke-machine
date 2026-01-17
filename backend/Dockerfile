
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY settings.gradle .
COPY build.gradle .


COPY gradle ./gradle


RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew bootJar --no-daemon


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]