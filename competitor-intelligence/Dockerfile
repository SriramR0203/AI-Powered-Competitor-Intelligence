# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Download dependencies first (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    apt-get update -qq && apt-get install -y --no-install-recommends maven && \
    mvn dependency:go-offline -q
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -q

# ── Stage 2: run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Non-root user for security
RUN groupadd --gid 1001 appgroup && \
    useradd  --uid 1001 --gid appgroup --shell /bin/false appuser

COPY --from=build --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
