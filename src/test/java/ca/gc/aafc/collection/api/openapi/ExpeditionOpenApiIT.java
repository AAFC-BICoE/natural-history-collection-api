package ca.gc.aafc.collection.api.openapi;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProjectTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;

import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class ExpeditionOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = ProjectDto.TYPENAME;

  protected ExpeditionOpenApiIT() {
    super("/api/v1/");
  }

  @Test
  @SneakyThrows
  void project_SpecValid() {
    ProjectDto projectDto = ProjectTestFixture.newProject();
    projectDto.setCreatedBy("test user");
    projectDto.setAttachment(null);

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "Expedition",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(projectDto),
      Map.of(
        "participants", JsonAPITestHelper.generateExternalRelationList("metadata", 1)
      ),
        null)
      ).extract().asString());
  }

}
