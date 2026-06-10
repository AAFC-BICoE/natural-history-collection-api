package ca.gc.aafc.collection.api.openapi;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.TestConfigProperties;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.repository.CollectionControlledVocabularyItemRepository;
import ca.gc.aafc.collection.api.repository.CollectionControlledVocabularyRepositoryIT;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
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
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(TestConfigProperties.class)
public class CollectingEventOpenApiIT extends BaseRestAssuredTest {

  @Inject
  private ServiceTransactionWrapper serviceTransactionWrapper;

  @Inject
  private CollectionControlledVocabularyItemRepository controlledVocabularyItemRepository;

  public static final String TYPE_NAME = "collecting-event";
  private static final String CREATED_BY = "test user";

  protected CollectingEventOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    createManagedAttribute();

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

  private void createManagedAttribute() {

    CollectionControlledVocabularyItemDto dto =
      CollectionControlledVocabularyItemTestFixture.newCollectionControlledVocabularyItem();

    dto.setName("key");
    dto.setGroup("group");
    dto.setVocabularyElementType(VocabularyElementType.STRING);
    dto.setAcceptedValues(null);
    dto.setDinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name());
    dto.setCreatedBy(CREATED_BY);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocumentWithRelToOne(
      null, CollectionControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto),
      Map.of("controlledVocabulary", JsonApiDocument.ResourceIdentifier.builder()
        .type(CollectionControlledVocabularyDto.TYPENAME)
        .id(CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID).build()
      )
    );

    serviceTransactionWrapper.executeWithParam( (p) ->
      controlledVocabularyItemRepository.create(p, null).getDto(), docToCreate);
  }

}
