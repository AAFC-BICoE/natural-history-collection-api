package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ExtensionValueTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.GoneException;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import javax.inject.Inject;
import javax.validation.ValidationException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture.RESTRICTION_FIELD_KEY;
import static ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture.RESTRICTION_VALUE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject
  private OrganismRepository organismRepository;

  @Inject
  private CollectingEventRepository eventRepository;

  @Inject
  private CollectionRepository collectionRepository;

  @Inject
  private CollectionManagedAttributeRepo collManagedAttributeRepo;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();

    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto)
    );

    UUID matSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(materialSampleToCreate));

    //"include=" + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM

    MaterialSampleDto result = materialSampleRepository.getOne(matSampleId, null).getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
    assertArrayEquals(MaterialSampleTestFixture.DWC_OTHER_CATALOG_NUMBERS, result.getDwcOtherCatalogNumbers());
    assertEquals(MaterialSampleTestFixture.GROUP, result.getGroup());
    assertEquals(MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME, result.getMaterialSampleName());
    assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    assertEquals(MaterialSampleTestFixture.ALLOW_DUPLICATE_NAME, result.getAllowDuplicateName());
    assertEquals(materialSampleDto.getBarcode(), result.getBarcode());
  //  assertEquals(1, result.getHierarchy().size());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void create_WithAParent() throws ResourceGoneException, ResourceNotFoundException {

    JsonApiDocument parentMaterialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample())
    );

    UUID parentMatSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(parentMaterialSampleToCreate));

    JsonApiDocument childMaterialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample()),
      Map.of("parentMaterialSample", JsonApiDocument.ResourceIdentifier.builder()
        .id(parentMatSampleId).type(MaterialSampleDto.TYPENAME).build())
    );

    UUID childMatSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(childMaterialSampleToCreate));

    MaterialSampleDto result = materialSampleRepository.getOne(parentMatSampleId, "include=materialSampleChildren").getDto();
    assertEquals(childMatSampleId, result.getMaterialSampleChildren().getFirst().getUuid());
  }

//  @Test
//  @WithMockKeycloakUser(groupRole = {"aafc:DINA_ADMIN"})
//  public void create_WithCollection_PersistedWithCollection() {
//    Institution institution = InstitutionFixture.newInstitutionEntity().build();
//    service.save(institution);
//    CollectionDto collectionDto = collectionRepository.create(CollectionFixture.newCollection()
//      .group("aafc")
//      .institution(InstitutionDto.builder().uuid(institution.getUuid()).build()).build());
//    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
//    materialSampleDto.setCollection(collectionDto);
//    QuerySpec querySpec = new QuerySpec(MaterialSampleDto.class);
//    querySpec.includeRelation(PathSpec.of(StorageUnitRepo.HIERARCHY_INCLUDE_PARAM));
//    assertEquals(collectionDto.getUuid(),
//      materialSampleRepository.findOne(
//        materialSampleRepository.create(materialSampleDto).getUuid(),querySpec).getCollection().getUuid());
//  }
//
//  @Test
//  @WithMockKeycloakUser(groupRole = {"aafc:user"})
//  public void create_recordCreated() {
//    CollectingEventDto event = eventRepository.findOne(
//            eventRepository.create(CollectingEventTestFixture.newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));
//    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
//    materialSampleDto.setCollectingEvent(event);
//    MaterialSampleDto result = materialSampleRepository.findOne(
//            materialSampleRepository.create(materialSampleDto).getUuid(),
//            new QuerySpec(MaterialSampleDto.class)
//    );
//    assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
//    assertEquals(event.getUuid(), result.getCollectingEvent().getUuid());
//    assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().get(0).getId());
//    assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
//  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = { "notAAFC:user" })
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    MaterialSample testMaterialSample = MaterialSampleFactory.newMaterialSample()
        .group(MaterialSampleTestFixture.GROUP).createdBy("dina").build();
    materialSampleService.create(testMaterialSample);

    MaterialSampleDto retrievedMaterialSample = materialSampleRepository
        .getOne(testMaterialSample.getUuid(), "").getDto();

    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedMaterialSample.getJsonApiId(), MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedMaterialSample)
    );

    assertThrows(AccessDeniedException.class,
        () -> materialSampleRepository.handleUpdate(docToUpdate, retrievedMaterialSample.getJsonApiId()));
  }

//  @Test
//  @WithMockKeycloakUser(groupRole = { "aafc:user" })
//  public void when_deleteAsUserFromMaterialSampleGroup_MaterialSampleDeleted()
//    throws ResourceGoneException, ResourceNotFoundException {
//    CollectingEventDto event = eventRepository
//        .findOne(eventRepository.create(CollectingEventTestFixture.newEventDto()).getUuid(),
//            new QuerySpec(CollectingEventDto.class));
//
//    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
//    materialSampleDto.setCollectingEvent(event);
//
//    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
//      UUID.randomUUID(), MaterialSampleDto.TYPENAME,
//      JsonAPITestHelper.toAttributeMap(materialSampleDto));
//
//    UUID materialSampleUuid =
//      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
//        .onCreate(docToCreate));
//
//    MaterialSampleDto result = materialSampleRepository.getOne(materialSampleUuid, "").getDto();
//
//    assertNotNull(result.getUuid());
//    materialSampleRepository.delete(result.getUuid());
//
//    assertThrows(ResourceGoneException.class, () -> materialSampleRepository.getOne(materialSampleUuid, ""));
//  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void when_InvalidRestrictionFieldExtension_ExceptionThrown(){
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();

    // Put an invalid key
    materialSampleDto.setRestrictionFieldsExtension(ExtensionValueTestFixture.newExtensionValue("ABC", RESTRICTION_FIELD_KEY, RESTRICTION_VALUE));

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto)
    );

    assertThrows(ValidationException.class, () -> materialSampleRepository.onCreate(docToCreate));
  }

//  @Test
//  @WithMockKeycloakUser(groupRole = { OrganismTestFixture.GROUP + ":user" })
//  public void updateMaterialSample_WithOrganism_accepted() throws MalformedURLException {
//
//    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
//    UUID organismUUID = organismRepository.create(organismDto).getUuid();
//    OrganismDto result = organismRepository.findOne(organismUUID,
//            new QuerySpec(OrganismDto.class));
//    assertNotNull(result.getCreatedBy());
//
//    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
//    UUID matSampleUUID = materialSampleRepository.create(materialSampleDto).getUuid();
//
//    MaterialSampleDto result2 = materialSampleRepository.findOne(matSampleUUID,
//            new QuerySpec(MaterialSampleDto.class));
//
//    result2.setOrganism(List.of(result));
//    materialSampleRepository.save(result2);
//
//    organismRepository.delete(organismUUID);
//  }

  @Test
  @WithMockKeycloakUser(groupRole = {CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER"})
  public void create_onManagedAttributeValue_validationOccur()
    throws ResourceGoneException, ResourceNotFoundException {

    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setVocabularyElementType(VocabularyElementType.DATE);
    newAttribute.setAcceptedValues(null);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(newAttribute)
    );
    newAttribute = collManagedAttributeRepo.create(docToCreate, null).getDto();

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setGroup(CollectionManagedAttributeTestFixture.GROUP);

    // Put an invalid value for Date
    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "zxy"));
    JsonApiDocument matSampleDocToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto)
    );
    assertThrows(ValidationException.class, () -> materialSampleRepository.onCreate(matSampleDocToCreate));

    // Fix the value
    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "2022-02-02"));
    JsonApiDocument matSampleDocToRetry = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto)
    );

    UUID matSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(matSampleDocToRetry));

    //cleanup
    materialSampleRepository.delete(matSampleId);

    // can't delete managed attribute for now since the check for key in use is using a fresh transaction
  }

  @Test
  @WithMockKeycloakUser(groupRole = {CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER"})
  public void create_onManagedAttributeValue_canUseKeyEvenIfUsedByAnotherComponent()
    throws ResourceGoneException, ResourceNotFoundException {

    CollectionManagedAttributeDto newAttributeCE = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttributeCE.setName("test");
    newAttributeCE.setVocabularyElementType(VocabularyElementType.DATE);
    newAttributeCE.setAcceptedValues(null);
    newAttributeCE.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(newAttributeCE)
    );
    collManagedAttributeRepo.onCreate(docToCreate);

    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setName("test");
    newAttribute.setVocabularyElementType(VocabularyElementType.DATE);
    newAttribute.setAcceptedValues(null);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);

    docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(newAttribute)
    );
    newAttribute = collManagedAttributeRepo.create(docToCreate, null).getDto();

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setGroup(CollectionManagedAttributeTestFixture.GROUP);

    materialSampleDto.setManagedAttributes(Map.of(newAttribute.getKey(), "2022-02-02"));

    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto)
    );

    UUID matSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(materialSampleToCreate));

    //cleanup
    materialSampleRepository.delete(matSampleId);

    // can't delete managed attribute for now since the check for key in use is using a fresh transaction
  }

}
