package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

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
    String ExpectedType = "host_of";
    String expectedRemarks = RandomStringUtils.randomAlphabetic(13);

    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .remarks(expectedRemarks)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    String sampleID = postSample(sample);
    findSample(sampleID)
      .body("data.attributes.associations.associatedSample", Matchers.contains(associatedWithId))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType))
      .body("data.attributes.associations.remarks", Matchers.contains(expectedRemarks));
  }

  @Test
  void post_withChild_andOrganisms() {
    // Step 1 - Create organisms.
    OrganismDto organism = new OrganismDto();
    organism.setGroup("aafc");

    String organismUuid1 = sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      OrganismDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(organism),
      null,
      null
    )).extract().body().jsonPath().getString("data.id");

    String organismUuid2 = sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      OrganismDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(organism),
      null,
      null
    )).extract().body().jsonPath().getString("data.id");

    // Step 2 - Create parent material sample with organisms attached.
    MaterialSampleDto parent = newSample();
    String parentId = sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(parent),
      Map.of(
        "organism", Map.of(
          "data", List.of(
            Map.of(
              "type", OrganismDto.TYPENAME,
              "id", organismUuid1
            ),
            Map.of(
              "type", OrganismDto.TYPENAME,
              "id", organismUuid2
            )
          )
        )
      ),
      null)
    ).extract().body().jsonPath().getString("data.id");

    // Step 3 - Create child material sample, linked to the parent material sample.
    MaterialSampleDto child = newSample();
    child.setMaterialSampleName("child");
    sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(child),
      JsonAPITestHelper.toRelationshipMap(
        List.of(
          JsonAPIRelationship.of("parentMaterialSample", MaterialSampleDto.TYPENAME, parentId)
        )
      ),
      null)
    );

    // Step 4 - GET request to see the number of material sample children.
    findSample(parentId).body("data.relationships.organism.data", Matchers.hasSize(2));
    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.hasSize(1));
  }

  @Test
  void patch_AddAssociation() {
    String ExpectedType = "host_of";
    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    String sampleID = postSample(sample);

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    sendPatch(sample, sampleID);

    findSample(sampleID)
      .body("data.attributes.associations.associatedSample", Matchers.contains(associatedWithId))
      .body("data.attributes.associations.associationType", Matchers.contains(ExpectedType));
  }

  @Test
  void patch_ChangAssociationType() {
    String associatedWithId = postSample(newSample());

    MaterialSampleDto sample = newSample();
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType("host_of")
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));
    String sampleID = postSample(sample);

    String ExpectedType = "parasite_of";
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    sendPatch(sample, sampleID);
    findSample(sampleID)
      .body("data.attributes.associations", Matchers.hasSize(1))
      .body("data.attributes.associations[0].associatedSample", Matchers.is(associatedWithId))
      .body("data.attributes.associations[0].associationType", Matchers.is(ExpectedType));
  }

  @Test
  void patch_SwapAssociation() {
    String associatedWithId = postSample(newSample());
    MaterialSampleDto sample = newSample();
    String sampleID = postSample(sample);

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType("host_of")
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));
    sendPatch(sample, sampleID);

    String updatedAssociationId = postSample(newSample());
    String newType = "has_host";

    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(newType)
      .associatedSample(UUID.fromString(updatedAssociationId))
      .build()));
    sendPatch(sample, sampleID);

    findSample(sampleID)
      .body("data.attributes.associations", Matchers.hasSize(1))
      .body("data.attributes.associations[0].associationType", Matchers.is(newType))
      .body("data.attributes.associations[0].associatedSample", Matchers.is(updatedAssociationId));
  }

  @Test
  void patch_withChild_childIgnored() {
    ImmutableMaterialSampleDto childDto = new ImmutableMaterialSampleDto();
    childDto.setUuid(UUID.fromString(postSample(newSample())));

    MaterialSampleDto parent = newSample();
    String parentId = postSample(parent);
    parent.setMaterialSampleChildren(List.of(childDto));

    sendPatch(parent, parentId);
    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.empty());
  }

  @Test
  void delete_withAssociation() {
    String ExpectedType = "host_of";
    MaterialSampleDto associatedWith = newSample();
    String associatedWithId = postSample(associatedWith);

    MaterialSampleDto sample = newSample();
    sample.setAssociations(List.of(AssociationDto.builder()
      .associationType(ExpectedType)
      .associatedSample(UUID.fromString(associatedWithId))
      .build()));

    String sampleID = postSample(sample);
    sendDelete(MaterialSampleDto.TYPENAME, sampleID);
  }

  private void sendPatch(MaterialSampleDto body, String id) {
    sendPatch(
      MaterialSampleDto.TYPENAME, id,
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(body),
        null,
        null),
      200
    );
  }

  private static MaterialSampleDto newSample() {
    MaterialSampleDto sampleDto = MaterialSampleTestFixture.newMaterialSample();
    sampleDto.setAttachment(null);
    sampleDto.setPreparedBy(null);
    sampleDto.setPreparationProtocol(null);
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
      .get(MaterialSampleDto.TYPENAME + "/" + unitId + "?include=organism,materialSampleChildren,parentMaterialSample"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

}
