package ca.gc.aafc.collection.api.openapi;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
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
public class ProtocolOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "protocol";

  protected ProtocolOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void protocol_SpecValid() {
    ProtocolDto protocolDto = ProtocolTestFixture.newProtocol();  
    protocolDto.setCreatedBy("test user");

    OpenAPI3Assertions
        .assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "Protocol",
            sendPost(TYPE_NAME, JsonAPITestHelper
                .toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(protocolDto)))
                .extract().asString());
  }
}
