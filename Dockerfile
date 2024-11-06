FROM eclipse-temurin:21-jre-jammy

RUN useradd -s /bin/bash user
USER user
COPY --chown=644 target/natural-history-collection-api-*.jar /natural-history-collection-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/natural-history-collection-api.jar"]
