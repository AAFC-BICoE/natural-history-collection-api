package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
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
public class StorageUnitRestIT extends BaseRestAssuredTest {

  private String parentId;
  private String childId;
  private StorageUnitDto unit;
  private StorageUnitDto parentUnit;
  private StorageUnitTypeDto unitType;
  private String unitId;
  private String unitTypeId;

  protected StorageUnitRestIT() {
    super("/api/v1/");
  }

  @BeforeEach
  void setUp() {
    parentUnit = newUnit();
    parentId = postUnit(parentUnit);
    childId = postUnit(newUnit());
    unitType = newUnitType();
    unitTypeId = postUnitType(unitType);
    unit = newUnit();
    unitId = postUnitWithRelations(parentId, unitTypeId, unit);
    sendPatchWithRelations(childId, unitId, unitTypeId);
  }

  @Test
  void find_WithRelations() {
    findUnit(unitId)
        .log().everything()
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue())
      .body("data.storageUnitChildren.data[0].id", Matchers.is(childId))
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(parentId))
      .body("data.relationships.storageUnitType.data.id", Matchers.is(unitTypeId));
    findUnit(childId)
      .body("data.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(unitId));
    findUnit(parentId)
      .body("data.storageUnitChildren.data[0].id", Matchers.is(unitId))
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void find_LoadsFlattenHierarchy() {
    findUnit(unitId)
      .body("data.attributes.hierarchy", Matchers.notNullValue())
      .body("data.attributes.hierarchy[0].uuid", Matchers.is(unitId))
      .body("data.attributes.hierarchy[0].name", Matchers.is(unit.getName()))
      .body("data.attributes.hierarchy[0].rank", Matchers.notNullValue())
      .body("data.attributes.hierarchy[0].type", Matchers.notNullValue())
      .body("data.attributes.hierarchy[0].typeName", Matchers.is(unitType.getName()))
      .body("data.attributes.hierarchy[0].typeUuid", Matchers.is(unitTypeId))
      .body("data.attributes.hierarchy[0].hierarchicalObject", Matchers.nullValue())
      .body("data.attributes.hierarchy[1].uuid", Matchers.is(parentId))
      .body("data.attributes.hierarchy[1].name", Matchers.is(parentUnit.getName()))
      .body("data.attributes.hierarchy[1].rank", Matchers.notNullValue())
      .body("data.attributes.hierarchy[1].hierarchicalObject", Matchers.nullValue());
  }

  @Test
  void patch_WithNewRelations() {
    String newParentId = postUnit(newUnit());
    //String newChildId = postUnit(newUnit());
    String newUnitTypeId = postUnitType(newUnitType());
    sendPatchWithRelations(unitId, newParentId, newUnitTypeId);
    findUnit(childId)
      //.body("data.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
    findUnit(parentId)
        .log().everything()
     // .body("data.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
//    findUnit(newChildId)
//      .body("data.storageUnitChildren.data", Matchers.empty())
//      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(unitId));
    findUnit(newParentId)
      .body("data.storageUnitChildren.data[0].id", Matchers.is(unitId))
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
    findUnit(unitId)
      .body("data.relationships.storageUnitType.data.id", Matchers.is(newUnitTypeId));
  }

  @Test
  void delete_RelationsResolved() {
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
      .get(StorageUnitDto.TYPENAME + "/" + unitId + "?include=storageUnitType,parentStorageUnit,"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

  private void sendPatchWithRelations(String sourceUnitId, String newParentId, String newUnitTypeId) {
    sendPatch(StorageUnitDto.TYPENAME, sourceUnitId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        getRelationshipMap(newParentId, newUnitTypeId),
        null)
    );
  }

  private String postUnitWithRelations(String parentId, String unitTypeId, StorageUnitDto unit) {
    return sendPost(StorageUnitDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(unit),
      getRelationshipMap(parentId, unitTypeId),
      null)
    ).extract().body().jsonPath().getString("data.id");
  }

  private String postUnit(StorageUnitDto unit) {
    return sendPost(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitDto.TYPENAME, unit)
    ).extract().body().jsonPath().getString("data.id");
  }

  private String postUnitType(StorageUnitTypeDto unitType) {
    return sendPost(
      StorageUnitTypeDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitTypeDto.TYPENAME, unitType)
    ).extract().body().jsonPath().getString("data.id");
  }

  private StorageUnitDto newUnit() {
    StorageUnitDto unitDto = new StorageUnitDto();
    unitDto.setName(RandomStringUtils.randomAlphabetic(3));
    unitDto.setGroup(RandomStringUtils.randomAlphabetic(4));
    unitDto.setStorageUnitChildren(null);
    unitDto.setParentStorageUnit(null);
    unitDto.setStorageUnitType(null);
    return unitDto;
  }

  private StorageUnitTypeDto newUnitType() {
    StorageUnitTypeDto unitTypeDto = new StorageUnitTypeDto();
    unitTypeDto.setName(RandomStringUtils.randomAlphabetic(3));
    unitTypeDto.setGroup(RandomStringUtils.randomAlphabetic(4));
    return unitTypeDto;
  }

  private Map<String, Object> getRelationshipMap(String newParentId, String newUnitTypeId) {
    return Map.of(
      "parentStorageUnit",
      Map.of("data", Map.of("type", StorageUnitDto.TYPENAME, "id", newParentId)),
      "storageUnitType",
      Map.of("data", Map.of("type", StorageUnitTypeDto.TYPENAME, "id", newUnitTypeId)));
  }
}
