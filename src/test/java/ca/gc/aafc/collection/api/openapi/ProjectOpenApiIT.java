package ca.gc.aafc.collection.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
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
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class ProjectOpenApiIT extends BaseRestAssuredTest {
  
  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = createSchemaUriBuilder(SPEC_HOST, SPEC_PATH);

  public static final String TYPE_NAME = ProjectDto.TYPENAME;

  protected ProjectOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @Test
  @SneakyThrows
  void project_SpecValid() {
    MaterialSampleDto ms = MaterialSampleTestFixture.newMaterialSample();
    ms.setAttachment(null);
    ms.setPreparedBy(null);
    ms.setPreparationAttachment(null);
    ms.setManagedAttributes(null);
    ms.setDetermination(null);
    ms.setOrganism(null);
    ms.setScheduledActions(null);
    ms.setHostOrganism(null);
    ms.setAcquisitionEvent(null);
    ms.setProjects(null);

    ProjectDto projectDto = ProjectTestFixture.newProject();  
    projectDto.setCreatedBy("test user");  
    projectDto.setAttachment(null);

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "Project",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(projectDto),
      Map.of(
        "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1)
      ),
        null)
      ).extract().asString());
  }

}