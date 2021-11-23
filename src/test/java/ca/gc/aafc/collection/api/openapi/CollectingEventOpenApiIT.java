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
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
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

public class CollectingEventOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "collecting-event";

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected CollectingEventOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName("key");
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeDto.setCreatedBy("dina");     

    sendPost("managed-attribute", JsonAPITestHelper.toJsonAPIMap("managed-attribute", JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto)));

    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();

    ce.setManagedAttributes(Map.of("key", "anything"));
    ce.setGeoReferenceAssertions(null);
    ce.setAttachment(null);
    ce.setCollectors(null);

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "CollectingEvent",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(ce),
        Map.of(
          "collectors", JsonAPITestHelper.generateExternalRelationList("person", 1),
          "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1)),
        null)
      ).extract().asString());
  }

}
