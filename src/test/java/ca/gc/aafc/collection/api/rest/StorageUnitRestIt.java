package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class StorageUnitRestIt extends BaseRestAssuredTest {

  private String parentId;
  private String childId;
  private StorageUnitDto unit;
  private String unitId;

  protected StorageUnitRestIt() {
    super("/api/v1/");
  }

  @BeforeEach
  void setUp() {
    StorageUnitDto parent = newUnit();
    parentId = postUnit(parent);

    StorageUnitDto child = newUnit();
    childId = postUnit(child);

    unit = newUnit();
    unitId = postUnitWithRelations(parentId, childId, unit);
  }

  @Test
  void find() {
    findUnit(unitId)
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue())
      .body("data.relationships.storageUnitChildren.data[0].id", Matchers.is(childId))
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(parentId));
    findUnit(childId)
      .body("data.relationships.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(unitId));
    findUnit(parentId)
      .body("data.relationships.storageUnitChildren.data[0].id", Matchers.is(unitId))
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void delete() {
    sendDelete(StorageUnitDto.TYPENAME, unitId);
    findUnit(childId)
      .body("data.relationships.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
    findUnit(parentId)
      .body("data.relationships.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  private ValidatableResponse findUnit(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(StorageUnitDto.TYPENAME + "/" + unitId + "?include=parentStorageUnit,storageUnitChildren").then();
  }

  private String postUnitWithRelations(String parentId, String childId, StorageUnitDto unit) {
    return sendPost(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        Map.of(
          "parentStorageUnit",
          Map.of("data", Map.of("type", StorageUnitDto.TYPENAME, "id", parentId)),
          "storageUnitChildren",
          Map.of("data", List.of(Map.of("type", StorageUnitDto.TYPENAME, "id", childId)))),
        null)
    ).extract().body().jsonPath().getString("data.id");
  }

  private String postUnit(StorageUnitDto unit) {
    return sendPost(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitDto.TYPENAME, unit)
    ).extract().body().jsonPath().getString("data.id");
  }

  private StorageUnitDto newUnit() {
    StorageUnitDto unitDto = new StorageUnitDto();
    unitDto.setName(RandomStringUtils.randomAlphabetic(3));
    unitDto.setGroup(RandomStringUtils.randomAlphabetic(4));
    unitDto.setStorageUnitChildren(null);
    unitDto.setParentStorageUnit(null);
    return unitDto;
  }

}
