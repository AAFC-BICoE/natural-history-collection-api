package ca.gc.aafc.collection.api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class CollectionControlledVocabularyItemRestIT extends BaseRestAssuredTest {

  protected CollectionControlledVocabularyItemRestIT() {
    super("/api/v1/");
  }

  @Test
  public void testPost() {

    CollectionControlledVocabularyDto vocabDto = CollectionControlledVocabularyTestFixture.newCollectionControlledVocabulary();

    String controlledVocabularyUuid = sendPost(CollectionControlledVocabularyDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      CollectionControlledVocabularyDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(vocabDto),
      null,
      null)
    ).extract().body().jsonPath().getString("data.id");

    CollectionControlledVocabularyItemDto vocabItemDto =
      CollectionControlledVocabularyItemTestFixture.newCollectionControlledVocabularyItem();
    vocabItemDto.setGroup("dev");

    String controlledVocabularyItemUuid = sendPost(CollectionControlledVocabularyItemDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      CollectionControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(vocabItemDto),
      JsonAPITestHelper.toRelationshipMap(JsonAPIRelationship.of("controlledVocabulary", CollectionControlledVocabularyDto.TYPENAME, controlledVocabularyUuid)),
      null)
    ).extract().body().jsonPath().getString("data.id");

    sendGet(CollectionControlledVocabularyItemDto.TYPENAME, controlledVocabularyItemUuid);

  }

}
