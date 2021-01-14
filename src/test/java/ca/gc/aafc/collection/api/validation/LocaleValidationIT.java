package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class LocaleValidationIT {

  @LocalServerPort
  protected int testPort;

  @SneakyThrows
  @Test
  void validate_LocaleSwitch_LocalResolved() {
    CollectingEvent event = CollectingEvent.builder()
      .createdBy("jon")
      .startEventDateTime(null)
      .endEventDateTime(LocalDateTime.of(1900, 1, 1, 1, 1))
      .build();

    RestAssured.given()
      .port(this.testPort)
      .contentType("application/vnd.api+json")
      .header("Accept-Language", "fr")
      .when()
      .body(JsonAPITestHelper.toJsonAPIMap(
        "collecting-event",
        UUID.randomUUID().toString(),
        JsonAPITestHelper.toAttributeMap(event)))
      .post("/api/v1/collecting-event/")
      .then()
      .statusCode(422).log().all(true);

  }

}
