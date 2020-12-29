package ca.gc.aafc.collection.api.exceptionmapping;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.transaction.Transactional;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
class DateTimeParsingExceptionMapperTest extends BaseRestAssuredTest {

  protected DateTimeParsingExceptionMapperTest() {
    super("");
  }

  @Test
  void exceptionMappedTest() {
    sendPost(
      "/api/v1/collecting-event",
      JsonAPITestHelper.toJsonAPIMap(
        "collecting-event",
        new ImmutableMap.Builder<String, Object>()
          .put("endEventDateTime", "asdasdasdasd")
          .build()),
      422
    );
  }
}