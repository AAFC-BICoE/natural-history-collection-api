package ca.gc.aafc.collection.api.openapi;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ScheduledActionDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample.MaterialSampleType;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProjectTestFixture;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.testsupport.specs.ValidationRestrictionOptions;

import io.restassured.RestAssured;
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

  @SneakyThrows
  @Test
  void materialSample_SpecValid() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName("name");
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    collectionManagedAttributeDto.setCreatedBy("dina");     

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

    OrganismDto organism = OrganismTestFixture.newOrganism(determination);

    MaterialSampleDto ms = MaterialSampleTestFixture.newMaterialSample();
    ms.setMaterialSampleType(MaterialSampleType.MIXED_ORGANISMS);
    ms.setAttachment(null);
    ms.setPreparedBy(null);
    ms.setPreparationAttachment(null);
    ms.setManagedAttributes(Map.of("name", "anything"));
    ms.setOrganism(null);
    ms.setScheduledActions(List.of(scheduledAction));
    ms.setHostOrganism(hostOrganism);
    ms.setAcquisitionEvent(null);
    ms.setProjects(null);

    MaterialSampleDto parent = MaterialSampleTestFixture.newMaterialSample();
    parent.setMaterialSampleType(MaterialSampleType.MOLECULAR_SAMPLE);
    parent.setDwcCatalogNumber("parent" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    parent.setMaterialSampleName("parent" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME);
    parent.setAttachment(null);
    parent.setParentMaterialSample(null);
    parent.setMaterialSampleChildren(null);
    parent.setPreparedBy(null);
    parent.setPreparationAttachment(null);
    parent.setAcquisitionEvent(null);
    parent.setProjects(null);

    MaterialSampleDto child = MaterialSampleTestFixture.newMaterialSample();
    child.setMaterialSampleType(MaterialSampleType.WHOLE_ORGANISM);
    child.setDwcCatalogNumber("child" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    child.setMaterialSampleName("child" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME); 
    child.setAttachment(null);
    child.setParentMaterialSample(null);
    child.setMaterialSampleChildren(null);
    child.setPreparedBy(null);
    child.setPreparationAttachment(null);
    child.setAcquisitionEvent(null);
    child.setProjects(null);

    ProjectDto projectDto = ProjectTestFixture.newProject();  
    projectDto.setCreatedBy("test user");  
    projectDto.setAttachment(null);

    PreparationTypeDto preparationTypeDto = PreparationTypeTestFixture.newPreparationType();  
    preparationTypeDto.setCreatedBy("test user");  
    
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

    String preparationTypeUUID = JsonAPITestHelper.extractId(sendPost(
      PreparationTypeDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        PreparationTypeDto.TYPENAME, 
        JsonAPITestHelper.toAttributeMap(preparationTypeDto)
      )
    ));

    String projectUUID = JsonAPITestHelper.extractId(sendPost(
      ProjectDto.TYPENAME, 
      JsonAPITestHelper.toJsonAPIMap(
        ProjectDto.TYPENAME, 
        JsonAPITestHelper.toAttributeMap(projectDto),
        Map.of(
          "attachment", 
          JsonAPITestHelper.generateExternalRelationList("metadata", 1)
        ),
      null)
    ));

    String organismUUID = JsonAPITestHelper.extractId(sendPost(
      OrganismDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(
        OrganismDto.TYPENAME, 
        JsonAPITestHelper.toAttributeMap(organism)
      )
    ));

    Map<String, Object> attributeMap = JsonAPITestHelper.toAttributeMap(ms);
    Map<String, Object> generatedRelationshipMap = Map.of(
      "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1),
      "preparedBy", JsonAPITestHelper.generateExternalRelation("person"),
      "preparationAttachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1),
      "projects", getRelationshipListType("project", projectUUID),
      "organism", getRelationshipListType("organism", organismUUID));
    Map<String, Object> relationshipMapWithId = JsonAPITestHelper.toRelationshipMap(
        List.of(
          JsonAPIRelationship.of("preparationType", PreparationTypeDto.TYPENAME, preparationTypeUUID),
          JsonAPIRelationship.of("parentMaterialSample", MaterialSampleDto.TYPENAME, parentUUID)));

    Map<String, Object> relationshipMap = new HashMap<>(generatedRelationshipMap);
    relationshipMap.putAll(relationshipMapWithId);

    String unitId = JsonAPITestHelper.extractId(sendPost(
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
      JsonAPITestHelper.toRelationshipMap(JsonAPIRelationship.of("parentMaterialSample", MaterialSampleDto.TYPENAME, unitId)),
      null
    ));
    
    // Included relationships with the request
    Set<String> toInclude = new HashSet<>();
    toInclude.addAll(generatedRelationshipMap.keySet());
    toInclude.addAll(relationshipMapWithId.keySet());
    toInclude.add("materialSampleChildren");
    toInclude.add("organism");
    toInclude.add(StorageUnitRepo.HIERARCHY_INCLUDE_PARAM);

    OpenAPI3Assertions.assertRemoteSchema(
        OpenAPIConstants.COLLECTION_API_SPECS_URL,
      "MaterialSample", 
      RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
        .get(MaterialSampleDto.TYPENAME + "/" + unitId + "?include=" + String.join(",", toInclude)).asString(),
      ValidationRestrictionOptions.builder().allowAdditionalFields(false).allowableMissingFields(Set.of("collectingEvent", "acquisitionEvent")).build()
      );
    }

  private Map<String, Object> getRelationshipListType(String type, String uuid) {
    return Map.of("data", List.of(Map.of(
      "id", uuid,
      "type", type)));
  }

}
