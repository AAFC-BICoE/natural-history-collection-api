package ca.gc.aafc.collection.api.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitUsageTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.testsupport.specs.ValidationRestrictionOptions;

import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class StorageUnitUsageOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = StorageUnitUsageDto.TYPENAME;

  protected StorageUnitUsageOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void storageUnitUsage_SpecValid() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    String unitTypeUuid = postResource(StorageUnitTypeDto.TYPENAME, storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setCreatedBy("test user");
    storageUnitDto.setBarcode("test barcode");
    storageUnitDto.setParentStorageUnit(null);
    storageUnitDto.setStorageUnitType(null);
    storageUnitDto.setStorageUnitChildren(null);

    String unitUuid = JsonAPITestHelper.extractId(sendPost(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(storageUnitDto),
        JsonAPITestHelper.toRelationshipMap(
          List.of(JsonAPIRelationship.of("storageUnitType", StorageUnitTypeDto.TYPENAME, unitTypeUuid))),
        null
      )));

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    dto.setStorageUnit(null);
    dto.setStorageUnitType(null);

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "StorageUnitUsage",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(dto),
        JsonAPITestHelper.toRelationshipMap(List.of(JsonAPIRelationship.of("storageUnit", StorageUnitDto.TYPENAME, unitUuid))),
        null)
      ).extract().asString(), ValidationRestrictionOptions.builder().allowableMissingFields(Set.of("storageUnitType", "cellNumber")).build());
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
