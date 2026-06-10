package ca.gc.aafc.collection.api.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.TestConfigProperties;
import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.repository.CollectionControlledVocabularyItemRepository;
import ca.gc.aafc.collection.api.repository.CollectionControlledVocabularyRepositoryIT;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;

import jakarta.inject.Inject;
import java.util.Map;
import lombok.SneakyThrows;

@SpringBootTest(
        classes = CollectionModuleApiLauncher.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
@Import(TestConfigProperties.class)
public class AssemblageOpenApiIT extends BaseRestAssuredTest {

  @Inject
  private ServiceTransactionWrapper serviceTransactionWrapper;

  @Inject
  private CollectionControlledVocabularyItemRepository controlledVocabularyItemRepository;

  public static final String TYPE_NAME = AssemblageDto.TYPENAME;

  private static final String MA_KEY = "key_assemblage";

  protected AssemblageOpenApiIT() {
    super("/api/v1/");
  }

  @Test
  @SneakyThrows
  void assemblage_SpecValid() {

    createManagedAttribute(MA_KEY);

    AssemblageDto assemblageDto = AssemblageTestFixture.newAssemblage();
    assemblageDto.setManagedAttributes(Map.of(MA_KEY, "anything"));
    assemblageDto.setCreatedBy("test user");
    assemblageDto.setAttachment(null);

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "Assemblage",
            sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(assemblageDto),
                    Map.of(
                            "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1)
                    ),
                    null)
            ).extract().asString());
  }

  private void createManagedAttribute(String key) {

    CollectionControlledVocabularyItemDto dto =
      CollectionControlledVocabularyItemTestFixture.newCollectionControlledVocabularyItem();

    dto.setName(key);
    dto.setGroup("group");
    dto.setVocabularyElementType(VocabularyElementType.STRING);
    dto.setAcceptedValues(null);
    dto.setDinaComponent(CollectionVocabularyConfiguration.DinaComponent.ASSEMBLAGE.name());
    dto.setCreatedBy("dina");

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
