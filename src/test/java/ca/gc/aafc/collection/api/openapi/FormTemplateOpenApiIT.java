package ca.gc.aafc.collection.api.openapi;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.FormTemplateFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;

import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class FormTemplateOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = FormTemplateDto.TYPENAME;
  public static final String SCHEMA_NAME = "FormTemplate";

  protected FormTemplateOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void formTemplate_SpecValid() {
    FormTemplateDto dto = FormTemplateFixture.newFormTemplate().createdBy("test").build();

    ValidatableResponse apiResponse = sendPost(TYPE_NAME, JsonAPITestHelper
        .toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(dto), null, null));

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, SCHEMA_NAME, apiResponse.extract().asString(), null);
  }

}
