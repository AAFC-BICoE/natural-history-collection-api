package ca.gc.aafc.collection.api.openapi;

//import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;

import java.util.Properties;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
//@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(StorageUnitTypeOpenApiIT.CollectionModuleTestConfiguration.class)
public class StorageUnitTypeOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = StorageUnitTypeDto.TYPENAME;

  protected StorageUnitTypeOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void storageUnitType_SpecValid() {
    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "StorageUnitType",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(storageUnitTypeDto))
      ).extract().asString());
  }

  @TestConfiguration
  public static class CollectionModuleTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "collection-module-version");
      return new BuildProperties(props);
    }
  }
}
