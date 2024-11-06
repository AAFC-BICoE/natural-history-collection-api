package ca.gc.aafc.collection.api.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.dto.ScheduledActionDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample.MaterialSampleType;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationMethodTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProjectTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitUsageTestFixture;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.testsupport.specs.ValidationRestrictionOptions;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.SneakyThrows;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class MaterialSampleOpenApiIT extends BaseRestAssuredTest {

  protected MaterialSampleOpenApiIT() {
    super("/api/v1/");
  }

  private static final String CREATED_BY = "test user";

  @SneakyThrows
  @Test
  void materialSample_SpecValid() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName("name");
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setVocabularyElementType(VocabularyElementType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    collectionManagedAttributeDto.setCreatedBy(CREATED_BY);

    sendPost("managed-attribute", JsonAPITestHelper.toJsonAPIMap("managed-attribute", JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto)));

    HostOrganism hostOrganism = HostOrganism.builder()
      .name("host name")
      .remarks("host remark")
      .build();
    
    ScheduledActionDto scheduledAction = ScheduledActionDto.builder()
      .actionStatus("actionStatus")
      .date(LocalDate.now())
      .actionType("actionType")
      .remarks("remarks")
      .assignedTo(ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("user").build())
      .build();

    Determination determination = DeterminationFactory.newDetermination()
      .isPrimary(true)
      .verbatimScientificName("verbatimScientificName")
      .build();

    OrganismDto organismDto = OrganismTestFixture.newOrganism(determination);
    organismDto.setIsTarget(true);

    MaterialSampleDto ms = MaterialSampleTestFixture.newMaterialSample();
    ms.setMaterialSampleType(MaterialSampleType.MIXED_ORGANISMS);
    ms.setManagedAttributes(Map.of("name", "anything"));
    ms.setScheduledActions(List.of(scheduledAction));
    ms.setHostOrganism(hostOrganism);
    setRelationshipsToNull(ms);

    MaterialSampleDto parent = MaterialSampleTestFixture.newMaterialSample();
    parent.setMaterialSampleType(MaterialSampleType.MOLECULAR_SAMPLE);
    parent.setDwcCatalogNumber("parent" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    parent.setMaterialSampleName("parent" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME);
    setRelationshipsToNull(parent);

    MaterialSampleDto child = MaterialSampleTestFixture.newMaterialSample();
    child.setMaterialSampleType(MaterialSampleType.WHOLE_ORGANISM);
    child.setDwcCatalogNumber("child" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    child.setMaterialSampleName("child" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME);
    setRelationshipsToNull(child);

    ProjectDto projectDto = ProjectTestFixture.newProject();  
    projectDto.setCreatedBy(CREATED_BY);
    projectDto.setAttachment(null);

    CollectionDto collectionDto = CollectionFixture.newCollection()
            .build();
    collectionDto.setCreatedBy(CREATED_BY);

    AssemblageDto assemblageDto = AssemblageTestFixture.newAssemblage();
    assemblageDto.setCreatedBy(CREATED_BY);
    assemblageDto.setAttachment(null);

    PreparationTypeDto preparationTypeDto = PreparationTypeTestFixture.newPreparationType();  
    preparationTypeDto.setCreatedBy(CREATED_BY);

    PreparationMethodDto preparationMethodDto = PreparationMethodTestFixture.newPreparationMethod();
    preparationMethodDto.setCreatedBy(CREATED_BY);

    ProtocolDto protocolDto = ProtocolTestFixture.newProtocol();
    protocolDto.setAttachments(null);
    protocolDto.setCreatedBy(CREATED_BY);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    String storageUnitUUID = postResource(StorageUnitDto.TYPENAME, storageUnitDto);

    StorageUnitUsageDto storageUnitUsageDto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    storageUnitUsageDto.setStorageUnit(null);
    storageUnitUsageDto.setStorageUnitType(null);
    storageUnitUsageDto.setWellRow(null);
    storageUnitUsageDto.setWellColumn(null);

    String storageUnitUsageUUID = JsonAPITestHelper.extractId(sendPost(
      StorageUnitUsageDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        StorageUnitUsageDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(storageUnitUsageDto),
        JsonAPITestHelper.toRelationshipMap(JsonAPIRelationship.of("storageUnit", StorageUnitDto.TYPENAME, storageUnitUUID)),
      null
      )
    ));

    String parentUUID = JsonAPITestHelper.extractId(sendPost(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME, 
        JsonAPITestHelper.toAttributeMap(parent)
      )
    ));

    String childUUID = JsonAPITestHelper.extractId(sendPost(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME, 
        JsonAPITestHelper.toAttributeMap(child)
      )
    ));

    String preparationTypeUUID = postResource(PreparationTypeDto.TYPENAME, preparationTypeDto);
    String preparationMethodUUID = postResource(PreparationMethodDto.TYPENAME, preparationMethodDto);
    String protocolUUID = postResource(ProtocolDto.TYPENAME, protocolDto);
    String organismUUID = postResource(OrganismDto.TYPENAME, organismDto);
    String assemblageUUID = postResource(AssemblageDto.TYPENAME, assemblageDto);
    String projectUUID = postResource(ProjectDto.TYPENAME, projectDto);
    String collectionUUID = postResource(CollectionDto.TYPENAME, collectionDto);

    Map<String, Object> attributeMap = JsonAPITestHelper.toAttributeMap(ms);
    Map<String, Object> toManyRelationships = Map.of(
      "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1),
      "preparedBy", JsonAPITestHelper.generateExternalRelation("person"),
      "projects", getRelationshipListType("project", projectUUID),
      "assemblages", getRelationshipListType("assemblage", assemblageUUID),
      "organism", getRelationshipListType("organism", organismUUID)
    );

    Map<String, Object> toOneRelationships = JsonAPITestHelper.toRelationshipMap(
        List.of(
          JsonAPIRelationship.of("preparationType", PreparationTypeDto.TYPENAME, preparationTypeUUID),
          JsonAPIRelationship.of("preparationMethod", PreparationMethodDto.TYPENAME, preparationMethodUUID),
          JsonAPIRelationship.of("parentMaterialSample", MaterialSampleDto.TYPENAME, parentUUID),
          JsonAPIRelationship.of("preparationProtocol", ProtocolDto.TYPENAME, protocolUUID),
          JsonAPIRelationship.of("storageUnitUsage", StorageUnitUsageDto.TYPENAME, storageUnitUsageUUID),
          JsonAPIRelationship.of("collection", CollectionDto.TYPENAME, collectionUUID)));

    Map<String, Object> relationshipMap = new HashMap<>(toManyRelationships);
    relationshipMap.putAll(toOneRelationships);

    String materialSampleId = JsonAPITestHelper.extractId(sendPost(
      MaterialSampleDto.TYPENAME, 
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME, 
        attributeMap,
        relationshipMap,
        null
      )
    ));

    sendPatch(MaterialSampleDto.TYPENAME, childUUID, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      Map.of(),
      JsonAPITestHelper.toRelationshipMap(JsonAPIRelationship.of("parentMaterialSample", MaterialSampleDto.TYPENAME, materialSampleId)),
      null
    ));
    
    // Included relationships with the request
    Set<String> toInclude = new HashSet<>();
    toInclude.addAll(toManyRelationships.keySet());
    toInclude.addAll(toOneRelationships.keySet());
    toInclude.add("materialSampleChildren");
    toInclude.add(StorageUnitRepo.HIERARCHY_INCLUDE_PARAM);

    OpenAPI3Assertions.assertRemoteSchema(
        OpenAPIConstants.COLLECTION_API_SPECS_URL,
      "MaterialSample", 
      RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
        .get(MaterialSampleDto.TYPENAME + "/" + materialSampleId + "?include=" + String.join(",", toInclude)).asString(),
      ValidationRestrictionOptions.builder()
        .allowAdditionalFields(false)
        .allowableMissingFields(Set.of("collectingEvent", "acquisitionEvent", "organismPrimaryDetermination", "storageUnitCoordinates"))
        .build()
      );
    }

  private String postResource(String resourceType, Object dto) {
    return JsonAPITestHelper.extractId(sendPost(
            resourceType,
            JsonAPITestHelper.toJsonAPIMap(
                    resourceType,
                    JsonAPITestHelper.toAttributeMap(dto))
    ));
  }

  /**
   * Set {@link MaterialSampleDto} relationships to null, so they won't be serialized as attributes.
   *
   * @param materialSampleDto
   */
  private void setRelationshipsToNull(MaterialSampleDto materialSampleDto) {
    materialSampleDto.setAttachment(null);
    materialSampleDto.setParentMaterialSample(null);
    materialSampleDto.setMaterialSampleChildren(null);
    materialSampleDto.setPreparedBy(null);
    materialSampleDto.setPreparationProtocol(null);
    materialSampleDto.setProjects(null);
    materialSampleDto.setAssemblages(null);
    materialSampleDto.setCollection(null);
  }

  private Map<String, Object> getRelationshipListType(String type, String uuid) {
    return Map.of("data", List.of(Map.of(
      "id", uuid,
      "type", type)));
  }

}
