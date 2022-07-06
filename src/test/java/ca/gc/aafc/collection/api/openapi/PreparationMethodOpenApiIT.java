package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationMethodTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class PreparationMethodOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "preparation-method";

  protected PreparationMethodOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void preparationMethod_SpecValid() {

    PreparationMethodDto preparationMethodDto = PreparationMethodTestFixture.newPreparationMethod();
    preparationMethodDto.setCreatedBy("test user");

    OpenAPI3Assertions
        .assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "PreparationMethod",
                sendPost(TYPE_NAME, JsonAPITestHelper
                .toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationMethodDto)))
                .extract().asString());
  }
}
