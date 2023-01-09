package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest(
        classes = CollectionModuleApiLauncher.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class AssemblageOpenApiIT extends BaseRestAssuredTest {

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
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName(key);
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setManagedAttributeType(VocabularyElementType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.ASSEMBLAGE);
    collectionManagedAttributeDto.setCreatedBy("dina");

    sendPost("managed-attribute", JsonAPITestHelper.toJsonAPIMap("managed-attribute", JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto)));
  }

}
