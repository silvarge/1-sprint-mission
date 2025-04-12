# Build Stage
FROM amazoncorretto:17 AS builder

WORKDIR /app
COPY . /app

RUN yum update -y && yum install -y bash curl unzip \
    && chmod +x ./gradlew \
    && ./gradlew clean bootJar --no-daemon

# Runtime Stage
FROM amazoncorretto:17

ARG PROJECT_NAME
ARG PROJECT_VERSION

LABEL project_name=${PROJECT_NAME}
LABEL project_version=${PROJECT_VERSION}

WORKDIR /app

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
CMD wget --quiet --tries=1 --spider http://localhost:80/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]
