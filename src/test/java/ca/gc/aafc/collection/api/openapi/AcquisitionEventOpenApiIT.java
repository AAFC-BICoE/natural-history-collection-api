package ca.gc.aafc.collection.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AcquisitionEventDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.AcquisitionEventTestFixture;
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
public class AcquisitionEventOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = createSchemaUriBuilder(SPEC_HOST, SPEC_PATH);

  public static final String TYPE_NAME = AcquisitionEventDto.TYPENAME;

  protected AcquisitionEventOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void acquisitionEvent_SpecValid() {
    AcquisitionEventDto acquisitionEventDto = AcquisitionEventTestFixture.newAcquisitionEvent();  
    acquisitionEventDto.setCreatedBy("test user");  
    acquisitionEventDto.setReceivedFrom(null);
    acquisitionEventDto.setExternallyIsolatedBy(null);

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "AcquisitionEvent",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(acquisitionEventDto),
      Map.of(
        "receivedFrom", getRelationType("person", UUID.randomUUID().toString()),
        "externallyIsolatedBy", getRelationType("person", UUID.randomUUID().toString())
      ),
        null)
      ).log().all().extract().asString());
  }

  private Map<String, Object> getRelationType(String type, String uuid) {
    return Map.of("data", Map.of(
      "id", uuid,
      "type", type));
  }
  
}