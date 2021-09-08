package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.ImmutableStorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class StorageUnitRestIT extends BaseRestAssuredTest {

  protected StorageUnitRestIT() {
    super("/api/v1/");
  }

  @Test
  void find_WithRelations() {
    StorageUnitDto unit = newUnit();
    StorageUnitTypeDto unitType = newUnitType();
    String parentId = postUnit(newUnit());
    String childId = postUnit(newUnit());
    String unitTypeId = postUnitType(unitType);
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId), 201);
    sendPatchWithRelations(unit, childId, getRelationshipMap(unitId, unitTypeId));

    findUnit(unitId)
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue())
      .body("data.attributes.storageUnitChildren[0].uuid", Matchers.is(childId))
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(parentId))
      .body("data.relationships.storageUnitType.data.id", Matchers.is(unitTypeId));
    findUnit(childId)
      .body("data.attributes.storageUnitChildren", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(unitId));
    findUnit(parentId)
      .body("data.attributes.storageUnitChildren[0].uuid", Matchers.is(unitId))
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void find_LoadsFlattenHierarchy() {
    StorageUnitDto unit = newUnit();
    StorageUnitDto parentUnit = newUnit();
    StorageUnitTypeDto unitType = newUnitType();
    String parentId = postUnit(parentUnit);
    String unitTypeId = postUnitType(unitType);
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId), 201);
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
    StorageUnitDto unit = newUnit();
    String unitId = postUnit(unit);

    findUnit(unitId)
      .body("data.relationships.storageUnitType.data", Matchers.nullValue());

    String newUnitTypeId = postUnitType(newUnitType());
    sendPatchWithRelations(unit, unitId, getUnitTypeRelationshipMap(newUnitTypeId));

    findUnit(unitId)
      .body("data.relationships.storageUnitType.data.id", Matchers.is(newUnitTypeId));
  }

  @Test
  void patch_CyclicParent_Returns400BadRequest() {
    String parentId = postUnit(newUnit());
    String childId = postUnit(newUnit(), getParentStorageUnitRelationshipMap(parentId), 201);
    String secondChildId = postUnit(newUnit(), getParentStorageUnitRelationshipMap(childId), 201);
    sendPatch(StorageUnitDto.TYPENAME, parentId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        Map.of(),
        getParentStorageUnitRelationshipMap(secondChildId),
        null)
    , 400);
  }

  @Test
  void post_CyclicParent_Returns400BadRequest() {
    StorageUnitDto parent = newUnit();
    String parentId = postUnit(parent);
    parent.setUuid(UUID.fromString(parentId));

    String childId = postUnit(newUnit(), getParentStorageUnitRelationshipMap(parentId), 201);

    StorageUnitDto second = newUnit();

    ImmutableStorageUnitDto parentDto = new ImmutableStorageUnitDto();
    parentDto.setUuid(parent.getUuid());
    second.setStorageUnitChildren(List.of(parentDto));

   postUnit(second, getParentStorageUnitRelationshipMap(childId), 400);
  }

  @Test
  void delete_RelationsResolved() {
    StorageUnitDto unit = newUnit();
    String parentId = postUnit(newUnit());
    String childId = postUnit(newUnit());
    String unitTypeId = postUnitType(newUnitType());
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId), 201);
    sendPatchWithRelations(unit, childId, getParentStorageUnitRelationshipMap(unitId));
    sendDelete(StorageUnitDto.TYPENAME, unitId);
    findUnit(childId)
      .body("data.relationships.storageUnitChildren.data", Matchers.nullValue())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
    findUnit(parentId)
      .body("data.relationships.storageUnitChildren.data", Matchers.nullValue())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  private ValidatableResponse findUnit(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(StorageUnitDto.TYPENAME + "/" + unitId + "?include=storageUnitType,parentStorageUnit,"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

  private void sendPatchWithRelations(StorageUnitDto unit, String sourceUnitId, Map<String,Object> relationshipMap) {
    sendPatch(StorageUnitDto.TYPENAME, sourceUnitId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        relationshipMap,
        null)
    );
  }

  private String postUnit(StorageUnitDto unit, Map<String, Object> relationshipMap, int expectedCode) {
    return sendPost(StorageUnitDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        relationshipMap,
        null),
      expectedCode
    ).extract().body().jsonPath().getString("data.id");
  }

  private String postUnit(StorageUnitDto unit) {
    return postUnit(unit, null, 201);
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

  private Map<String, Object> getUnitTypeRelationshipMap(String newUnitTypeId) {
    return Map.of(
      "storageUnitType",
      Map.of("data", Map.of("type", StorageUnitTypeDto.TYPENAME, "id", newUnitTypeId)));
  }

  private Map<String, Object> getParentStorageUnitRelationshipMap(String newParentId) {
    return Map.of(
      "parentStorageUnit",
      Map.of("data", Map.of("type", StorageUnitDto.TYPENAME, "id", newParentId)));
  }
}
