package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitUsageTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(CollectionModuleBaseIT.CollectionModuleTestConfiguration.class)
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
  void get_withNonExistingInclude_NoError() {
    // Step 1 - Create a material sample
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");

      String sampleId = JsonAPITestHelper.extractId(
        sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
          MaterialSampleDto.TYPENAME,
          JsonAPITestHelper.toAttributeMap(sample),
          null,
          null)
        ));

    // Step 2 - Get the material sample with include=collectingEvent
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "attachment"),
      200
    );

    response.body("data.id", Matchers.is(sampleId));
  }

  @Test
  void get_withStorageUnitUsage_NoError() {
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

    // Step 3 - Create a storage unit usage linked to the storage unit
    StorageUnitUsageDto storageUnitUsage = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnit);

    String storageUnitUsageId = JsonAPITestHelper.extractId(
      sendPost(StorageUnitUsageDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        StorageUnitUsageDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(storageUnitUsage),
        JsonAPITestHelper.toRelationshipMap(
          JsonAPIRelationship.of("storageUnit", StorageUnitDto.TYPENAME, storageUnitId)
        ),
        null)
      ));

    // Step 4 - Create a material sample linked to the storage unit usage
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");

    String sampleId = JsonAPITestHelper.extractId(
      sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sample),
        JsonAPITestHelper.toRelationshipMap(
          JsonAPIRelationship.of("storageUnitUsage", StorageUnitUsageDto.TYPENAME, storageUnitUsageId)
        ),
        null)
      ));

    // Step 5 - Get the material sample with include=storageUnitUsage
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "storageUnitUsage"),
      200
    );

    response.body("data.id", Matchers.is(sampleId));
    response.body("data.relationships.storageUnitUsage.data.id", Matchers.is(storageUnitUsageId));
  }

  @Test
  void get_withOrganismInclude_determinationLoaded() {
    // Step 1 - Create an organism with determination
    Determination determination = DeterminationFactory.newDetermination().build();
    OrganismDto organism = OrganismTestFixture.newOrganism(determination);
    String organismId = JsonAPITestHelper.extractId(
      sendPost(OrganismDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        OrganismDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(organism),
        null,
        null)
      ));

    // Step 2 - Create a material sample linked to the organism
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");
    sample.setOrganism(List.of(OrganismDto.builder().uuid(UUID.fromString(organismId)).build())); 
    String sampleId = JsonAPITestHelper.extractId(
      sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sample),
        JsonAPITestHelper.toRelationshipMapByName(
          List.of(JsonAPIRelationship.of("organism", OrganismDto.TYPENAME, organismId))
        ),
        null)
      ));

    // Step 3 - Get the material sample with include=organism
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "organism"),
      200
    );

    // Step 4 - Organism and determination are included
    response.body("data.id", Matchers.is(sampleId));
    response.body("included.find { it.type == 'organism' }.id", Matchers.is(organismId));
    response.body("included.find { it.type == 'organism' }.attributes.determination.size()", 
                Matchers.greaterThan(0));
    response.body("included.find { it.type == 'organism' }.attributes.determination[0].verbatimScientificName", 
                Matchers.is(determination.getVerbatimScientificName()));
    response.body("included.find { it.type == 'organism' }.attributes.determination[0].scientificNameDetails.sourceUrl", 
                Matchers.is(determination.getScientificNameDetails().getSourceUrl()));
  }

  @Test
  void get_withExistingExternalInclude_NoError() {
    // Step 1 - Create a material sample
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");

    String sampleId = JsonAPITestHelper.extractId(
      sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sample),
        JsonAPITestHelper.toRelationshipMapByName(
          List.of(JsonAPIRelationship.of("attachment", "metadata", UUID.randomUUID().toString()))
        ),
        null)
      ));

    // Step 2 - Get the material sample with include=attachment
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "attachment"),
      200
    );

    response.body("data.id", Matchers.is(sampleId));
  }

  @Test
  public void get_withExistingAssemblageInclude_NoError() {
    // Step 1 - Create an assemblage
    String assemblageId = JsonAPITestHelper.extractId(
      sendPost(AssemblageDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        AssemblageDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(AssemblageTestFixture.newAssemblage()),
        null,
        null)
      ));

    // Step 2 - Create a material sample linked to the assemblage
    MaterialSampleDto sample = newSample();
    sample.setMaterialSampleName("Sample1");

    String sampleId = JsonAPITestHelper.extractId(
      sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sample),
        JsonAPITestHelper.toRelationshipMapByName(
          List.of(JsonAPIRelationship.of("assemblages", AssemblageDto.TYPENAME, assemblageId))
        ),
        null)
      ));

    // Step 3 - Get the material sample with include=assemblage
    ValidatableResponse response = sendGet(
      MaterialSampleDto.TYPENAME,
      sampleId,
      Map.of("include", "assemblages"),
      200
    );

    response.body("data.id", Matchers.is(sampleId));
    response.body("data.relationships.assemblages.data.id", Matchers.contains(assemblageId));
  }

  @Test
  public void bulkCreateUpdateBulkLoad_HttpOkReturned() {
    MaterialSampleDto msDto1 = newSample();
    MaterialSampleDto msDto2 = newSample();

    JsonApiBulkDocument bulkDocumentCreate = JsonApiBulkDocument.builder()
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(MaterialSampleDto.TYPENAME)
        .attributes(JsonAPITestHelper.toAttributeMap(msDto1)).build())
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(MaterialSampleDto.TYPENAME)
        .attributes(JsonAPITestHelper.toAttributeMap(msDto2)).build())
      .build();

    var response = sendBulkCreate(MaterialSampleDto.TYPENAME, bulkDocumentCreate);
    List<String> ids = response.extract().body().jsonPath().getList("data.id");
    assertEquals(2, ids.size());

    UUID uuid1 = UUID.fromString(ids.get(0));
    UUID uuid2 = UUID.fromString(ids.get(1));

    JsonApiBulkDocument bulkDocumentUpdate = JsonApiBulkDocument.builder()
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(MaterialSampleDto.TYPENAME)
        .id(uuid1)
        .attributes(JsonAPITestHelper.toAttributeMap(msDto1)).build())
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(MaterialSampleDto.TYPENAME)
        .id(uuid2)
        .attributes(JsonAPITestHelper.toAttributeMap(msDto2)).build())
      .build();
    sendBulkUpdate(MaterialSampleDto.TYPENAME, bulkDocumentUpdate);

    sendBulkLoad(MaterialSampleDto.TYPENAME, JsonApiBulkResourceIdentifierDocument.builder()
      .addData(JsonApiDocument.ResourceIdentifier.builder()
        .type(MaterialSampleDto.TYPENAME)
        .id(uuid1).build())
      .addData(JsonApiDocument.ResourceIdentifier.builder()
        .type(MaterialSampleDto.TYPENAME)
        .id(uuid2).build())
      .build());
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
