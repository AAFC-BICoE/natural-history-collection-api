package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.repository.GoneException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import javax.validation.ValidationException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject
  private CollectingEventRepository eventRepository;

  @Inject
  private CollectionRepository collectionRepository;

  @Inject
  private CollectionManagedAttributeRepo collManagedAttributeRepo;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    QuerySpec querySpec = new QuerySpec(MaterialSampleDto.class);
    querySpec.includeRelation(PathSpec.of(StorageUnitRepo.HIERARCHY_INCLUDE_PARAM));
    MaterialSampleDto result = materialSampleRepository.findOne(materialSampleRepository.create(materialSampleDto).getUuid(),
            querySpec);
    assertNotNull(result.getCreatedBy());
    assertEquals(materialSampleDto.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
    assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
    assertEquals(MaterialSampleTestFixture.DWC_OTHER_CATALOG_NUMBERS, result.getDwcOtherCatalogNumbers());
    assertEquals(MaterialSampleTestFixture.GROUP, result.getGroup());
    assertEquals(MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME, result.getMaterialSampleName());
    assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().getId());
    assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    assertEquals(MaterialSampleTestFixture.ALLOW_DUPLICATE_NAME, result.getAllowDuplicateName());
    assertEquals(materialSampleDto.getBarcode(), result.getBarcode());
    assertEquals(1, result.getHierarchy().size());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void create_WithAParent() {
    MaterialSampleDto parent = materialSampleRepository.create(MaterialSampleTestFixture.newMaterialSample());
    MaterialSampleDto child = MaterialSampleTestFixture.newMaterialSample();
    child.setParentMaterialSample(parent);
    child = materialSampleRepository.create(child);
    MaterialSampleDto result = materialSampleRepository.findOne(parent.getUuid(), new QuerySpec(MaterialSampleDto.class));
    assertEquals(child.getUuid(), result.getMaterialSampleChildren().get(0).getUuid());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:DINA_ADMIN"})
  public void create_WithCollection_PersistedWithCollection() {
    Institution institution = InstitutionFixture.newInstitutionEntity().build();
    service.save(institution);
    CollectionDto collectionDto = collectionRepository.create(CollectionFixture.newCollection()
      .group("aafc")
      .institution(InstitutionDto.builder().uuid(institution.getUuid()).build()).build());
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setCollection(collectionDto);
    QuerySpec querySpec = new QuerySpec(MaterialSampleDto.class);
    querySpec.includeRelation(PathSpec.of(StorageUnitRepo.HIERARCHY_INCLUDE_PARAM));
    assertEquals(collectionDto.getUuid(),
      materialSampleRepository.findOne(
        materialSampleRepository.create(materialSampleDto).getUuid(),querySpec).getCollection().getUuid());
  }

    @Test
    @WithMockKeycloakUser(groupRole = {"aafc:user"})
    public void create_recordCreated() {
        CollectingEventDto event = eventRepository.findOne(
            eventRepository.create(CollectingEventTestFixture.newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));
        MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
        materialSampleDto.setCollectingEvent(event);
        MaterialSampleDto result = materialSampleRepository.findOne(
            materialSampleRepository.create(materialSampleDto).getUuid(),
            new QuerySpec(MaterialSampleDto.class)
            );
        assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
        assertEquals(event.getUuid(), result.getCollectingEvent().getUuid());
        assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().getId());
        assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = { "notAAFC:user" })
  public void updateFromDifferentGroup_throwAccessDenied() {
    MaterialSample testMaterialSample = MaterialSampleFactory.newMaterialSample()
        .group(MaterialSampleTestFixture.GROUP).createdBy("dina").build();
    materialSampleService.create(testMaterialSample);
    MaterialSampleDto retrievedMaterialSample = materialSampleRepository
        .findOne(testMaterialSample.getUuid(), new QuerySpec(MaterialSampleDto.class));
    assertThrows(AccessDeniedException.class,
        () -> materialSampleRepository.save(retrievedMaterialSample));
  }

  @Test
  @WithMockKeycloakUser(groupRole = { "aafc:user" })
  public void when_deleteAsUserFromMaterialSampleGroup_MaterialSampleDeleted() {
    CollectingEventDto event = eventRepository
        .findOne(eventRepository.create(CollectingEventTestFixture.newEventDto()).getUuid(),
            new QuerySpec(CollectingEventDto.class));
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setCollectingEvent(event);

    MaterialSampleDto result = materialSampleRepository
        .findOne(materialSampleRepository.create(materialSampleDto).getUuid(),
            new QuerySpec(MaterialSampleDto.class));

    assertNotNull(result.getUuid());
    materialSampleRepository.delete(result.getUuid());
    assertThrows(GoneException.class, () -> materialSampleRepository
        .findOne(result.getUuid(), new QuerySpec(MaterialSampleDto.class)));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void when_InvalidRestrictionFieldExtension_ExceptionThrown(){
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();

    // Put an invalid key
    materialSampleDto.getRestrictionFieldsExtension().get(0).setExtKey("ABC");
    assertThrows(ValidationException.class, () -> materialSampleRepository.create(materialSampleDto));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER"})
  public void create_onManagedAttributeValue_validationOccur() {

    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.DATE);
    newAttribute.setAcceptedValues(null);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);

    newAttribute = collManagedAttributeRepo.create(newAttribute);

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setGroup(CollectionManagedAttributeTestFixture.GROUP);

    // Put an invalid value for Date
    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "zxy"));
    assertThrows(ValidationException.class, () -> materialSampleRepository.create(materialSampleDto));

    // Fix the value
    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "2022-02-02"));
    UUID matSampleId = materialSampleRepository.create(materialSampleDto).getUuid();

    //cleanup
    materialSampleRepository.delete(matSampleId);

    // can't delete managed attribute for now since the check for key in use is using a fresh transaction
  }

  @Test
  @WithMockKeycloakUser(groupRole = {CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER"})
  public void create_onManagedAttributeValue_canUseKeyEvenIfUsedByAnotherComponent() {

    CollectionManagedAttributeDto newAttributeCE = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttributeCE.setName("test");
    newAttributeCE.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.DATE);
    newAttributeCE.setAcceptedValues(null);
    newAttributeCE.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collManagedAttributeRepo.create(newAttributeCE);

    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setName("test");
    newAttribute.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.DATE);
    newAttribute.setAcceptedValues(null);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    newAttribute = collManagedAttributeRepo.create(newAttribute);

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setGroup(CollectionManagedAttributeTestFixture.GROUP);

    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "2022-02-02"));
    UUID matSampleId = materialSampleRepository.create(materialSampleDto).getUuid();

    //cleanup
    materialSampleRepository.delete(matSampleId);

    // can't delete managed attribute for now since the check for key in use is using a fresh transaction
  }

}
