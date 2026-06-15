package ca.gc.aafc.collection.api.openapi;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.TestConfigProperties;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;

import java.util.Map;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(TestConfigProperties.class)
public class CollectionManagedAttributeOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "managed-attribute";

  protected CollectionManagedAttributeOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void CollectionControlledVocabulary_SpecValid() {

    CollectionControlledVocabularyItemDto dto =
      CollectionControlledVocabularyItemTestFixture.newCollectionControlledVocabularyItem();

    String uuid = JsonAPITestHelper.extractId(sendPost(
      CollectionControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        CollectionControlledVocabularyItemDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(dto),
        JsonAPITestHelper.toRelationshipMap(
          JsonAPIRelationship.of("controlledVocabulary", CollectionControlledVocabularyDto.TYPENAME,
            CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID.toString())),
        null
      )
    ));

//    OpenAPI3Assertions
//        .assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "CollectionManagedAttribute",
//            sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME,
//                JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto))).extract()
//                .asString());
  }

}
