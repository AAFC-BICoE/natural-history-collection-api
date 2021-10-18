package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class MaterialSampleRestIT extends BaseRestAssuredTest {

  protected MaterialSampleRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_withChild_childIgnored() {
    ImmutableMaterialSampleDto childDto = new ImmutableMaterialSampleDto();
    childDto.setUuid(UUID.fromString(postSample(newSample())));

    MaterialSampleDto parent = newSample();
    parent.setMaterialSampleChildren(List.of(childDto));

    String parentId = postSample(parent);

    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.empty());
  }

  @Test
  void post_withAssociation() {
    String ExpectedType = RandomStringUtils.randomAlphabetic(4);
    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    String sampleID = postSample(sample);
    findSample(sampleID)
      .body("data.attributes.associations.associatedSample", Matchers.contains(associatedWithId))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType));
    findSample(associatedWithId)
      .body("data.attributes.associations.associatedSample", Matchers.contains(sampleID))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType));
  }

  @Test
  void patch_withAssociation() {
    String ExpectedType = RandomStringUtils.randomAlphabetic(4);
    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    String sampleID = postSample(sample);

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    sendPatch(sample, sampleID, 200);

    findSample(sampleID)
      .body("data.attributes.associations.associatedSample", Matchers.contains(associatedWithId))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType));
    findSample(associatedWithId)
      .body("data.attributes.associations.associatedSample", Matchers.contains(sampleID))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType));
  }

  @Test
  void patch_SwapAssociation() {
    String associatedWithId = postSample(newSample());
    MaterialSampleDto sample = newSample();
    String sampleID = postSample(sample);

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(RandomStringUtils.randomAlphabetic(4))
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));
    sendPatch(sample, sampleID, 200);

    findSample(sampleID).body("data.attributes.associations", Matchers.hasSize(1));
    findSample(associatedWithId).body("data.attributes.associations", Matchers.hasSize(1));

    String updatedAssociationId = postSample(newSample());
    String newType = "newType";

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(newType)
      .associatedSample(UUID.fromString(updatedAssociationId))
      .build()));
    sendPatch(sample, sampleID, 200);

    findSample(sampleID).log().all(true)
      .body("data.attributes.associations", Matchers.hasSize(1))
      .body("data.attributes.associations[0].associationType", Matchers.is(newType))
      .body("data.attributes.associations[0].associatedSample", Matchers.is(updatedAssociationId));
  }

  @Test
  void association_UniqueConstraint() {
    String ExpectedType = RandomStringUtils.randomAlphabetic(4);
    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));
    String sampleID = postSample(sample);

    associatedWith.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(sampleID))
      .build()));

    sendPatch(associatedWith, associatedWithId, 422);
  }

  @Test
  void patch_withChild_childIgnored() {
    ImmutableMaterialSampleDto childDto = new ImmutableMaterialSampleDto();
    childDto.setUuid(UUID.fromString(postSample(newSample())));

    MaterialSampleDto parent = newSample();
    String parentId = postSample(parent);
    parent.setMaterialSampleChildren(List.of(childDto));

    sendPatch(parent, parentId, 200);
    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.empty());
  }

  private void sendPatch(MaterialSampleDto body, String id, int expectedCode) {
    sendPatch(
      MaterialSampleDto.TYPENAME, id,
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(body),
        null,
        null),
      expectedCode
    );
  }

  private static MaterialSampleDto newSample() {
    MaterialSampleDto sampleDto = MaterialSampleTestFixture.newMaterialSample();
    sampleDto.setAttachment(null);
    sampleDto.setPreparedBy(null);
    return sampleDto;
  }

  private String postSample(MaterialSampleDto parent) {
    return sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(parent),
      null,
      null)
    ).extract().body().jsonPath().getString("data.id");
  }

  private ValidatableResponse findSample(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(MaterialSampleDto.TYPENAME + "/" + unitId + "?include=materialSampleChildren"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

}
