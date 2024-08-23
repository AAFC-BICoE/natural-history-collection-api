package ca.gc.aafc.collection.api.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.SplitConfigurationDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.SplitConfigurationTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(ResourceNameIdentifierRepositoryIT.ResourceNameIdentifierRepositoryTestConfiguration.class)
public class SplitConfigurationRepositoryRestIT extends BaseRestAssuredTest {

  private static final String TYPE_NAME = SplitConfigurationDto.TYPENAME;

  protected SplitConfigurationRepositoryRestIT() {
    super("/api/v1/");
  }

  @Test
  void splitConfiguration_onCreateUpdate_separatorPreserved() {
    SplitConfigurationDto splitConfigurationDto =
      SplitConfigurationTestFixture.newSplitConfiguration();
    splitConfigurationDto.setSeparator(' ');

    String id = JsonAPITestHelper.extractId(
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(splitConfigurationDto))));

    sendGet(TYPE_NAME, id)
      .body("data.attributes.separator", Matchers.is(" "));

    sendPatch(
      TYPE_NAME, id,
      JsonAPITestHelper.toJsonAPIMap(
        TYPE_NAME,
        JsonAPITestHelper.toAttributeMap(splitConfigurationDto),
        null,
        null),
      200
    );

  }
}
