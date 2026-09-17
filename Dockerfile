FROM eclipse-temurin:25-jre
RUN useradd -r -u 10001 appuser
USER appuser
COPY --chown=appuser:appuser target/natural-history-collection-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
