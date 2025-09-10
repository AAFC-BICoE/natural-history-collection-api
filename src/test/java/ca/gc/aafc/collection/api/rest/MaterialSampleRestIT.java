package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
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

    String organismUuid1 = JsonAPITestHelper.extractId(
            sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
                    OrganismDto.TYPENAME,
                    JsonAPITestHelper.toAttributeMap(organism),
                    null,
                    null
            )));

    String organismUuid2 = JsonAPITestHelper.extractId(
            sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
                    OrganismDto.TYPENAME,
                    JsonAPITestHelper.toAttributeMap(organism),
                    null,
                    null
            )));

    String organismUuid3 = JsonAPITestHelper.extractId(
            sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
                    OrganismDto.TYPENAME,
                    JsonAPITestHelper.toAttributeMap(organism),
                    null,
                    null
            )));

    // Step 2 - Create parent material sample with organisms attached.
    MaterialSampleDto parent = newSample();
    String parentId = JsonAPITestHelper.extractId(
            sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
                    MaterialSampleDto.TYPENAME,
                    JsonAPITestHelper.toAttributeMap(parent),
                    JsonAPITestHelper.toRelationshipMapByName(
                            List.of(JsonAPIRelationship.of("organism", OrganismDto.TYPENAME, organismUuid1),
                                    JsonAPIRelationship.of("organism", OrganismDto.TYPENAME, organismUuid2),
                                    JsonAPIRelationship.of("organism", OrganismDto.TYPENAME, organismUuid3))),
                    null)
            ));

    // Step 3 - Create two child material samples, linked to the parent material sample.
    MaterialSampleDto child = newSample();
    child.setMaterialSampleName("child");

    sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(child),
      JsonAPITestHelper.toRelationshipMap(
        List.of(
          JsonAPIRelationship.of("parentMaterialSample", "material-sample", parentId)
        )
      ),
      null)
    );

    sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(child),
      JsonAPITestHelper.toRelationshipMap(
        List.of(
          JsonAPIRelationship.of("parentMaterialSample", "material-sample", parentId)
        )
      ),
      null)
    );

    // Step 4 - GET request to see the number of material sample children.
    findSample(parentId)
            .body("data.relationships.organism.data", Matchers.hasSize(3))
            .body("data.attributes.materialSampleChildren", Matchers.hasSize(2));
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

  @Test
  void get_withInclude() {
    // Step 1 - Create collecting event.
    CollectingEventDto collectingEvent = new CollectingEventDto();
    collectingEvent.setGroup("aafc");
    collectingEvent.setDwcRecordNumber("recordNumber");

    String collectingEventUUID = JsonAPITestHelper.extractId(
      sendPost(CollectingEventDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        CollectingEventDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(collectingEvent),
        null,
        null
      )));
    
    // Step 2 - Create a material sample with collecting event attached.
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");

    // Step 3 - Post the material sample.
    String sampleId = JsonAPITestHelper.extractId(
      sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sample),
        JsonAPITestHelper.toRelationshipMap(
          List.of(JsonAPIRelationship.of("collectingEvent", CollectingEventDto.TYPENAME, collectingEventUUID))
        ),
        null)
      ));

    // Step 4 - Get the material sample with include=collectingEvent
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "collectingEvent"),
      200
    );
    response.body("data.id", Matchers.is(sampleId));
    response.body("data.relationships.collectingEvent.data", Matchers.hasSize(1));
    response.body("included", Matchers.hasSize(1));
    response.body("included[0].id", Matchers.is(collectingEventUUID));
    response.body("included[0].attributes.dwcRecordNumber", Matchers.is(collectingEvent.getDwcRecordNumber()));
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
      .get(MaterialSampleDto.TYPENAME + "/" + unitId + "?include=" +
              String.join(",", "organism", "materialSampleChildren", "parentMaterialSample"
                      , StorageUnitRepo.HIERARCHY_INCLUDE_PARAM)).then();
  }

}
