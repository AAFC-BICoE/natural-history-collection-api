package ca.gc.aafc.collection.api.openapi;

import java.util.Map;

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

  public static final String TYPE_NAME = AcquisitionEventDto.TYPENAME;

  protected AcquisitionEventOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void acquisitionEvent_SpecValid() {
    AcquisitionEventDto acquisitionEventDto = AcquisitionEventTestFixture.newAcquisitionEvent();  
    acquisitionEventDto.setCreatedBy("test user");  
    acquisitionEventDto.setReceivedFrom(null);
    acquisitionEventDto.setIsolatedBy(null);

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "AcquisitionEvent",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(acquisitionEventDto),
      Map.of(
        "receivedFrom", JsonAPITestHelper.generateExternalRelation("person"),
        "isolatedBy", JsonAPITestHelper.generateExternalRelation("person")
      ),
        null)
      ).extract().asString());
  }

}
