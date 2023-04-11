package ca.gc.aafc.collection.api.openapi;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
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

  public static final String TYPE_NAME = "collecting-event";

  private static final String CREATED_BY = "test user";

  protected CollectingEventOpenApiIT() {
    super("/api/v1/");
  }


  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName("key");
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setVocabularyElementType(VocabularyElementType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeDto.setCreatedBy(CREATED_BY);

    sendPost("managed-attribute", JsonAPITestHelper.toJsonAPIMap("managed-attribute", JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto)));

    ProtocolDto protocolDto = ProtocolTestFixture.newProtocol();
    protocolDto.setProtocolType("collection_method");
    protocolDto.setAttachments(null);
    protocolDto.setCreatedBy(CREATED_BY);

    String protocolUUID = postResource("protocol", protocolDto);

    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();

    ce.setManagedAttributes(Map.of("key", "anything"));
    ce.setAttachment(null);
    ce.setCollectors(null);

    Map<String, Object> relationships = JsonAPITestHelper.toRelationshipMap(List.of(
      JsonAPIRelationship.of("protocol", ProtocolDto.TYPENAME, protocolUUID)
    ));

    relationships.putAll( Map.of(
      "collectors", JsonAPITestHelper.generateExternalRelationList("person", 1),
      "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1)));

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "CollectingEvent",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(ce),
        relationships,
        null)
      ).extract().asString());
  }
  private String postResource(String resourceType, Object dto) {
    return JsonAPITestHelper.extractId(sendPost(
      resourceType,
      JsonAPITestHelper.toJsonAPIMap(
        resourceType,
        JsonAPITestHelper.toAttributeMap(dto))
    ));
  }

}
