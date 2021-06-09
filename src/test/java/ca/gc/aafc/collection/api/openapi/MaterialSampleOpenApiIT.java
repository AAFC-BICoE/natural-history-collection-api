package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import io.restassured.response.ResponseBody;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})

public class MaterialSampleOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "material-sample";

  private static final String dwcCatalogNumber = "55342";
  private static final String materialSampleName = "S-412";

  private static final String createdBy = "test user";
  private static final String group = "test group";

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected MaterialSampleOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    MaterialSampleDto ms = new MaterialSampleDto();
    ms.setCreatedBy(createdBy);  
    ms.setGroup(group);  
    ms.setDwcCatalogNumber(dwcCatalogNumber);
    ms.setMaterialSampleName(materialSampleName);
    ms.setAttachment(null);
    ms.setCollectingEvent(null);
    ms.setParentMaterialSample(null);
    ms.setMaterialSampleChildren(null);

    MaterialSampleDto parent = new MaterialSampleDto();
    parent.setCreatedBy(createdBy);
    parent.setGroup(group);
    parent.setDwcCatalogNumber("parent" + dwcCatalogNumber);
    parent.setMaterialSampleName("parent" + materialSampleName);
    parent.setAttachment(null);
    parent.setCollectingEvent(null);
    parent.setParentMaterialSample(null);
    parent.setMaterialSampleChildren(null);

    MaterialSampleDto child = new MaterialSampleDto();
    child.setCreatedBy(createdBy);
    child.setGroup(group);
    child.setDwcCatalogNumber("child" + dwcCatalogNumber);
    child.setMaterialSampleName("child" + materialSampleName); 
    child.setAttachment(null);
    child.setCollectingEvent(null);
    child.setParentMaterialSample(null);
    child.setMaterialSampleChildren(null);
    
    sendPost("material-sample", JsonAPITestHelper.toJsonAPIMap("material-sample", JsonAPITestHelper.toAttributeMap(parent)));
    sendPost("material-sample", JsonAPITestHelper.toJsonAPIMap("material-sample", JsonAPITestHelper.toAttributeMap(child)));
    
    ResponseBody materialSampleResponseBody = sendGet("material-sample", "").extract().response().body();

    String parentUUID = materialSampleResponseBody.path("data[0].id");
    String childUUID = materialSampleResponseBody.path("data[1].id");

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "MaterialSample",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(ms),
      Map.of(
        "attachment", getRelationListType("metadata", UUID.randomUUID().toString()),
        "parentMaterialSample", getRelationType("material-sample", parentUUID),
        "materialSampleChildren", getRelationListType("material-sample", childUUID)),
        null)
      ).extract().asString(), false);
  }

  private Map<String, Object> getRelationType(String type, String uuid) {
    return Map.of("data", Map.of(
      "id", uuid,
      "type", type));
  }

  private Map<String, Object> getRelationListType(String type, String uuid) {

    return Map.of("data", List.of(Map.of(
      "id", uuid,
      "type", type)));
  }
}
