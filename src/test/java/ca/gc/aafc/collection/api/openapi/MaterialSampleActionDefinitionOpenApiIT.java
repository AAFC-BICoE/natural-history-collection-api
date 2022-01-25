package ca.gc.aafc.collection.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;
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
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class MaterialSampleActionDefinitionOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "material-sample-action-definition";

  private static final String name = "definition of ocean water isolation";
  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected MaterialSampleActionDefinitionOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setCreatedBy("test user");
    materialSampleActionDefinitionDto.setGroup("aafc");
    materialSampleActionDefinitionDto.setName(name);
    materialSampleActionDefinitionDto.setActionType(MaterialSampleActionDefinition.ActionType.ADD);
    materialSampleActionDefinitionDto.setFormTemplates(Map.of(MaterialSampleFormComponent.MATERIAL_SAMPLE, FormTemplate.builder()
      .allowNew(true)
      .allowExisting(true)
      .templateFields(Map.of("materialSampleName", TemplateField.builder()
        .enabled(true)  
        .defaultValue("test-default-value")
        .build()))
      .build()));
    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "MaterialSampleActionDefinition",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(materialSampleActionDefinitionDto),
        null,
        null)
      ).extract().asString());
  }

}
