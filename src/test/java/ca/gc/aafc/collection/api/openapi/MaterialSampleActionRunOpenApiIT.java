package ca.gc.aafc.collection.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionRunDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
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

public class MaterialSampleActionRunOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/materialSampleActionRun.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "material-sample-action-run";

  private static final UUID agentId = UUID.randomUUID();

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected MaterialSampleActionRunOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void materialSampleActionRun_SpecValid() {
    MaterialSampleActionRunDto materialSampleActionRunDto = new MaterialSampleActionRunDto();
    materialSampleActionRunDto.setCreatedBy("test user");  
    materialSampleActionRunDto.setMaterialSampleActionDefinition(null);
    materialSampleActionRunDto.setSourceMaterialSample(null);
    materialSampleActionRunDto.setAgentId(agentId);

    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setCreatedBy("materialSample test user");
    materialSampleActionDefinitionDto.setGroup("materialSample aafc");
    materialSampleActionDefinitionDto.setName("materialSample definition name");
    materialSampleActionDefinitionDto.setActionType(MaterialSampleActionDefinition.ActionType.ADD);
    materialSampleActionDefinitionDto.setFormTemplates(new HashMap<>(Map.of("materialSample", FormTemplate.builder()
      .allowNew(true)
      .allowExisting(true)
      .templateFields(new HashMap<>(Map.of("materialSampleName", TemplateField.builder()
        .enabled(true)  
        .defaultValue("test-default-value")
        .build())))
      .build())));

    MaterialSampleDto materialSampleDto = new MaterialSampleDto();
    materialSampleDto.setCreatedBy("test user");  
    materialSampleDto.setGroup("test group");  
    materialSampleDto.setDwcCatalogNumber("55342");
    materialSampleDto.setMaterialSampleName( "S-412");
    materialSampleDto.setAttachment(null);
    materialSampleDto.setCollectingEvent(null);
    materialSampleDto.setParentMaterialSample(null);
    materialSampleDto.setMaterialSampleChildren(null);

    sendPost("material-sample", JsonAPITestHelper.toJsonAPIMap("material-sample", JsonAPITestHelper.toAttributeMap(materialSampleDto)));
    sendPost("material-sample-action-definition", JsonAPITestHelper.toJsonAPIMap("material-sample-action-definition", JsonAPITestHelper.toAttributeMap(materialSampleActionDefinitionDto)));

    String materialSampleUUID = sendGet("material-sample", "").extract().response().body().path("data[0].id");
    String materialSampleActionDefinitionUUID = sendGet("material-sample-action-definition", "").extract().response().body().path("data[0].id");

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "MaterialSampleActionRun",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(materialSampleActionRunDto),
        Map.of(
          "sourceMaterialSample", getRelationshipType("material-sample", materialSampleUUID),
          "materialSampleActionDefinition", getRelationshipType("material-sample-action-definition", materialSampleActionDefinitionUUID)),
        null)
      ).extract().asString());
  }

  private Map<String, Object> getRelationshipType(String type, String uuid) {
    return Map.of("data", Map.of(
      "id", uuid,
      "type", type));
  }

}
