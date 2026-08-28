# -------------------------
# Stage 1: Gradle cache
# -------------------------
FROM amazoncorretto:21 AS cache

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY server/mystream/build.gradle.kts ./server/mystream/build.gradle.kts
COPY scripts/exclude-android-module.sh ./scripts/exclude-android-module.sh

RUN chmod +x gradlew
RUN chmod +x ./scripts/exclude-android-module.sh
RUN ./scripts/exclude-android-module.sh settings.gradle.kts

ENV GRADLE_USER_HOME=/gradle-cache

RUN ./gradlew :server:mystream:dependencies --no-daemon


# -------------------------
# Stage 2: Build
# -------------------------
FROM cache AS build

COPY server/mystream ./server/mystream

RUN ./gradlew :server:mystream:buildFatJar --no-daemon


# -------------------------
# Stage 3: Runtime
# -------------------------
FROM amazoncorretto:21 AS runtime

WORKDIR /app

COPY --from=build /app/server/mystream/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]