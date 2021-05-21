package ca.gc.aafc.collection.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
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

public class MaterialSampleOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  //TODO change to original DINA-WEB Repo
  private static final String SPEC_PATH = "luusteve/collection-specs/22764_Update_Open_Api_Specs-MaterialSample/schema/materialSample.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "material-sample";

  private static final String dwcCatalogNumber = "55342";
  private static final String materialSampleName = "S-412";



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
    ms.setCreatedBy("test user");  
    ms.setGroup("test group");  
    ms.setDwcCatalogNumber(dwcCatalogNumber);
    ms.setMaterialSampleName(materialSampleName);
    ms.setAttachment(null);
    ms.setCollectingEvent(null);
    ms.setParentMaterialSample(null);
    ms.setSubMaterialSample(null);


    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "MaterialSample",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(ms),
      Map.of(
        "attachment", getExternalListType("metadata")),
        null)
      ).extract().asString());
  }

  private Map<String, Object> getExternalListType(String type) {
    return Map.of("data", List.of(Map.of(
      "id", UUID.randomUUID().toString(),
      "type", type)));
  }

}
