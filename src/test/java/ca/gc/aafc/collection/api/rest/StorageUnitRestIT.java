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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.apache.commons.lang3.RandomStringUtils;

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
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId));
    sendPatchWithRelations(unit, childId, getRelationshipMap(unitId, unitTypeId));

    findUnit(unitId)
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue())
      .body("data.attributes.storageUnitChildren[0].id", Matchers.is(childId))
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(parentId))
      .body("data.relationships.storageUnitType.data.id", Matchers.is(unitTypeId));
    findUnit(childId)
      .body("data.attributes.storageUnitChildren", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(unitId));
    findUnit(parentId)
      .body("data.attributes.storageUnitChildren[0].id", Matchers.is(unitId))
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void find_LoadsFlattenHierarchy() {
    StorageUnitDto unit = newUnit();
    StorageUnitDto parentUnit = newUnit();
    StorageUnitTypeDto unitType = newUnitType();
    String parentId = postUnit(parentUnit);
    String unitTypeId = postUnitType(unitType);
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId));
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
  void post_withChild_UpdateIgnored() {
    StorageUnitDto child = newUnit();
    String childID = postUnit(child);
    child.setUuid(UUID.fromString(childID));

    StorageUnitDto parent = newUnit();

    ImmutableStorageUnitDto childDto = new ImmutableStorageUnitDto();
    childDto.setUuid(child.getUuid());
    parent.setStorageUnitChildren(List.of(childDto));

    findUnit(postUnit(parent)).body("data.attributes.storageUnitChildren", Matchers.empty());
  }

  @Test
  void patch_withChild_UpdateIgnored() {
    StorageUnitDto parent = newUnit();
    String parentID = postUnit(parent);

    StorageUnitDto child = newUnit();
    String childID = postUnit(child);
    child.setUuid(UUID.fromString(childID));

    ImmutableStorageUnitDto childDto = new ImmutableStorageUnitDto();
    childDto.setUuid(child.getUuid());
    parent.setStorageUnitChildren(List.of(childDto));

    sendPatch(StorageUnitDto.TYPENAME, parentID,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitDto.TYPENAME, JsonAPITestHelper.toAttributeMap(parent),
        null, null));

    findUnit(parentID).body("data.attributes.storageUnitChildren", Matchers.empty());
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
    String childId = postUnit(newUnit(), getParentStorageUnitRelationshipMap(parentId));
    String secondChildId = postUnit(newUnit(), getParentStorageUnitRelationshipMap(childId));
    sendPatch(StorageUnitDto.TYPENAME, parentId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        Map.of(),
        getParentStorageUnitRelationshipMap(secondChildId),
        null)
      , 400);
  }

  @Test
  void delete_RelationsResolved() {
    StorageUnitDto unit = newUnit();
    String parentId = postUnit(newUnit());
    String unitTypeId = postUnitType(newUnitType());
    String unitId = postUnit(unit, getRelationshipMap(parentId, unitTypeId));
    sendDelete(StorageUnitDto.TYPENAME, unitId);
    findUnit(parentId)
      .body("data.relationships.storageUnitChildren.data", Matchers.nullValue())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void post_CyclicParentOnOperationRequest_Returns400BadRequest() {
    String topId = postUnit(newUnit());
    String middleId = postUnit(newUnit());
    String bottomId = postUnit(newUnit());
    int returnCode = sendOperation(List.of(
      opPatchRelationMap(topId, middleId),
      opPatchRelationMap(middleId, bottomId),
      opPatchRelationMap(bottomId, topId)))
      .extract().jsonPath().getInt("status[2]");
    Assertions.assertEquals(400, returnCode);
  }

  private ValidatableResponse findUnit(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(StorageUnitDto.TYPENAME + "/" + unitId + "?include=storageUnitType,parentStorageUnit,storageUnitChildren,"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

  private void sendPatchWithRelations(
    StorageUnitDto unit,
    String sourceUnitId,
    Map<String, Object> relationshipMap
  ) {
    sendPatch(StorageUnitDto.TYPENAME, sourceUnitId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        relationshipMap,
        null)
    );
  }

  private String postUnit(StorageUnitDto unit, Map<String, Object> relationshipMap) {
    return sendPost(StorageUnitDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        relationshipMap,
        null),
      201
    ).extract().body().jsonPath().getString("data.id");
  }

  private String postUnit(StorageUnitDto unit) {
    return postUnit(unit, null);
  }

  private String postUnitType(StorageUnitTypeDto unitType) {
    return sendPost(
      StorageUnitTypeDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitTypeDto.TYPENAME, unitType)
    ).extract().body().jsonPath().getString("data.id");
  }

  private static StorageUnitDto newUnit() {
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

  private static Map<String, Object> opPatchRelationMap(String parentID, String resourceId) {
    return Map.of(
      "op", "PATCH",
      "path", StorageUnitDto.TYPENAME + "/" + resourceId,
      "value", Map.of(
        "id", resourceId,
        "type", StorageUnitDto.TYPENAME,
        "attributes", JsonAPITestHelper.toAttributeMap(newUnit()),
        "relationships", Map.of(
          "parentStorageUnit", Map.of(
            "data", Map.of(
              "type", "storage-unit",
              "id", parentID
            )))));
  }
}
