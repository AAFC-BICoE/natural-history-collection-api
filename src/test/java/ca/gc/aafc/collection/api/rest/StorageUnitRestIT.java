package ca.gc.aafc.collection.api.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.ImmutableStorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.repository.CollectionModuleBaseRepositoryIT;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(CollectionModuleBaseRepositoryIT.CollectionModuleTestConfiguration.class)
public class StorageUnitRestIT extends BaseRestAssuredTest {

  protected StorageUnitRestIT() {
    super("/api/v1/");
  }

  @Test
  void find_WithRelations() {
    StorageUnitDto unit = newUnit();
    StorageUnitTypeDto unitType = newUnitType();

    String parentId = postUnit(newUnit());
    StorageUnitDto childUnit = newUnit();
    String childId = postUnit(childUnit);
    String unitTypeId = postUnitType(unitType);
    String unitId = postUnit(unit, getRelationshipsMap(parentId, unitTypeId));

    findUnit(unitId)
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue())
      //.body("data.attributes.storageUnitChildren[0].id", Matchers.is(childId))
      .body("data.relationships.parentStorageUnit.data.id", Matchers.is(parentId))
      .body("data.relationships.storageUnitType.data.id", Matchers.is(unitTypeId));

    sendPatchWithRelations(childUnit, childId, getRelationshipsMap(unitId, unitTypeId));
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
    String unitId = postUnit(unit, getRelationshipsMap(parentId, unitTypeId));

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
        null, parentID));

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
    var response = sendPatch(StorageUnitDto.TYPENAME, parentId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        Map.of(),
        getParentStorageUnitRelationshipMap(secondChildId),
        parentId)
      , 400);

    assertTrue(response.extract().body().asString().contains("The parent is already present in the hierarchy"));
  }

  @Test
  void delete_RelationsResolved() {
    StorageUnitDto unit = newUnit();
    String parentId = postUnit(newUnit());
    String unitTypeId = postUnitType(newUnitType());
    String unitId = postUnit(unit, getRelationshipsMap(parentId, unitTypeId));
    sendDelete(StorageUnitDto.TYPENAME, unitId);
    findUnit(parentId)
      .body("data.relationships.storageUnitChildren.data", Matchers.empty())
      .body("data.relationships.parentStorageUnit.data", Matchers.nullValue());
  }

  @Test
  void post_CyclicParentOnOperationRequest_Returns400BadRequest() {
    String topId = postUnit(newUnit());
    String middleId = postUnit(newUnit());
    String bottomId = postUnit(newUnit());

    sendPatchRelationsOnly(topId, getParentStorageUnitRelationshipMap(middleId), HttpStatus.OK.value());
    sendPatchRelationsOnly(middleId, getParentStorageUnitRelationshipMap(bottomId), HttpStatus.OK.value());
    sendPatchRelationsOnly(bottomId, getParentStorageUnitRelationshipMap(topId), HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void get_withStorageUnitType_gridLayoutDefinitionLoaded() {
    // Step 1 - Create a storage unit type
    StorageUnitTypeDto storageUnitType = StorageUnitTypeTestFixture.newStorageUnitType();
    String storageUnitTypeId = JsonAPITestHelper.extractId(
      sendPost(StorageUnitTypeDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        StorageUnitTypeDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(storageUnitType),
        null,
        null)
      ));

    // Step 2 - Create a storage unit linked to the storage unit type
    StorageUnitDto storageUnit = StorageUnitTestFixture.newStorageUnit();
    String storageUnitId = JsonAPITestHelper.extractId(
      sendPost(StorageUnitDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(storageUnit),
        JsonAPITestHelper.toRelationshipMap(
          JsonAPIRelationship.of("storageUnitType", StorageUnitTypeDto.TYPENAME, storageUnitTypeId)
        ),
        null)
      ));

    // Step 3 - Get the storage unit with include=storageUnitType
    ValidatableResponse response = sendGet(
      StorageUnitDto.TYPENAME,
      storageUnitId,
      Map.of("include", "storageUnitType"),
      200
    );

    // Step 4 - Validate the response includes storage unit type data and gridLayoutDefinition
    response.body("data.id", Matchers.is(storageUnitId));
    response.body("data.relationships.storageUnitType.data.id", Matchers.is(storageUnitTypeId));
    response.body("included[0].id", Matchers.is(storageUnitTypeId));
    response.body("included[0].type", Matchers.is(StorageUnitTypeDto.TYPENAME));
    response.body("included[0].attributes.gridLayoutDefinition.numberOfRows", Matchers.is(storageUnitType.getGridLayoutDefinition().getNumberOfRows()));
    response.body("included[0].attributes.gridLayoutDefinition.numberOfColumns", Matchers.is(storageUnitType.getGridLayoutDefinition().getNumberOfColumns()));
    response.body("included[0].attributes.gridLayoutDefinition.fillDirection", Matchers.is(storageUnitType.getGridLayoutDefinition().getFillDirection().name()));
  }

  private ValidatableResponse findUnit(String unitId) {
    return RestAssured.given().port(this.testPort).basePath(this.basePath)
      .get(StorageUnitDto.TYPENAME + "/" + unitId + "?include=storageUnitType,parentStorageUnit,storageUnitChildren&"
        + "optfields[" + StorageUnitDto.TYPENAME + "]=" + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM
      ).then();
  }

  private void sendPatchWithRelations(
    StorageUnitDto unit,
    String sourceUnitId,
    Map<String, JsonApiDocument.RelationshipObject> relationshipMap
  ) {
    sendPatch(StorageUnitDto.TYPENAME, sourceUnitId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(unit),
        relationshipMap,
        sourceUnitId)
    );
  }

  private void sendPatchRelationsOnly(
    String unitId,
    Map<String, JsonApiDocument.RelationshipObject> relationshipMap,
    int expectedCode
  ) {
    sendPatch(StorageUnitDto.TYPENAME, unitId,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitDto.TYPENAME,
        Map.of(),
        relationshipMap,
        unitId),
      expectedCode
    );
  }

  private String postUnit(StorageUnitDto unit, Map<String, JsonApiDocument.RelationshipObject> relationshipMap) {
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
    StorageUnitDto unitDto = StorageUnitTestFixture.newStorageUnit();
    unitDto.setName(RandomStringUtils.randomAlphabetic(3));
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

  private Map<String, JsonApiDocument.RelationshipObject> getRelationshipsMap(String newParentId,
                                                            String newUnitTypeId) {
    return Map.of(
      "parentStorageUnit", JsonApiDocument.RelationshipObject.builder()
        .data(JsonApiDocument.ResourceIdentifier.builder().type(StorageUnitDto.TYPENAME).id(
          UUID.fromString(newParentId)).build()).build(),
      "storageUnitType", JsonApiDocument.RelationshipObject.builder()
        .data(JsonApiDocument.ResourceIdentifier.builder().type(StorageUnitTypeDto.TYPENAME).id(
          UUID.fromString(newUnitTypeId)).build()).build()
    );
  }

  private Map<String, JsonApiDocument.RelationshipObject> getUnitTypeRelationshipMap(String newUnitTypeId) {
    return Map.of(
      "storageUnitType", JsonApiDocument.RelationshipObject.builder()
        .data(JsonApiDocument.ResourceIdentifier.builder().type(StorageUnitTypeDto.TYPENAME).id(
          UUID.fromString(newUnitTypeId)).build()).build());
  }

  private Map<String, JsonApiDocument.RelationshipObject> getParentStorageUnitRelationshipMap(String newParentId) {
    return Map.of(
      "parentStorageUnit", JsonApiDocument.RelationshipObject.builder()
        .data(JsonApiDocument.ResourceIdentifier.builder().type(StorageUnitDto.TYPENAME).id(
          UUID.fromString(newParentId)).build()).build());
  }
}
