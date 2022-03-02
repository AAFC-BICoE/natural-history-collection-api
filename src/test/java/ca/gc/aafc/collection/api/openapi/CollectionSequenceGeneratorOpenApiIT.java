package ca.gc.aafc.collection.api.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
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
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class CollectionSequenceGeneratorOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "collection-sequence-generator";
  public static final String GROUP = "aafc";
  public static final int AMOUNT = 1;

  protected CollectionSequenceGeneratorOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void collectionSequenceGenerator_SpecValid() {
    // Create a collection to use for this test.
    CollectionDto collectionDto = CollectionFixture.newCollection().build();
    String collectionUUID = sendPost("collection", JsonAPITestHelper.toJsonAPIMap("collection", collectionDto))
        .extract().body().jsonPath().getString("data.id");
    
    // A blank dto with the default amount of 1.
    CollectionSequenceGeneratorDto collectionSequenceGeneratorDto = new CollectionSequenceGeneratorDto();

    // The collection id is set the "id" outside of the attributes. 
    // That is why it is not included for the JsonAPITestHelper.toAttributeMap().
    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "CollectionGeneratorResponse",
      sendPost(
        TYPE_NAME,
        JsonAPITestHelper.toJsonAPIMap(
          TYPE_NAME, 
          JsonAPITestHelper.toAttributeMap(collectionSequenceGeneratorDto), 
          null, 
          collectionUUID
        )
      ).extract().asString()
    );
  }
  
}
