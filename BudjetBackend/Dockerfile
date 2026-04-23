# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY gradlew.bat ./gradlew.bat
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew installDist --no-daemon -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/install/budgetapp/ /app/

EXPOSE 8080

ENTRYPOINT ["/app/bin/budgetapp"]
