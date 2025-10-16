package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ExtensionValueTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;

import static ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture.RESTRICTION_FIELD_KEY;
import static ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture.RESTRICTION_VALUE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

public class MaterialSampleRepositoryIT extends BaseRepositoryIT {

  @Inject
  protected MaterialSampleService materialSampleService;

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

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  @Transactional
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

    MaterialSampleDto result = materialSampleRepository.getOne(matSampleId,
        "optfields[" + "material-sample" + "]=" + MaterialSample.HIERARCHY_PROP_NAME)
      .getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
    assertArrayEquals(MaterialSampleTestFixture.DWC_OTHER_CATALOG_NUMBERS, result.getDwcOtherCatalogNumbers());
    assertEquals(MaterialSampleTestFixture.GROUP, result.getGroup());
    assertEquals(MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME, result.getMaterialSampleName());
    assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    assertEquals(MaterialSampleTestFixture.ALLOW_DUPLICATE_NAME, result.getAllowDuplicateName());
    assertEquals(materialSampleDto.getBarcode(), result.getBarcode());
    assertEquals(1, result.getHierarchy().size());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  @Transactional
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

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:DINA_ADMIN"})
  @Transactional
  public void create_WithCollection_PersistedWithCollection()
      throws ResourceGoneException, ResourceNotFoundException {

    UUID collectionUuid = createWithRepository(CollectionFixture
      .newCollection()
      .group("aafc")
      .build(), collectionRepository::onCreate);
    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample()),
      Map.of("collection", JsonApiDocument.ResourceIdentifier.builder()
        .id(collectionUuid).type(CollectionDto.TYPENAME).build())
    );

    UUID matSampleUuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));

    MaterialSampleDto result = materialSampleRepository.getOne(matSampleUuid, "include=collection").getDto();
    assertEquals(collectionUuid, result.getCollection().getUuid());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  @Transactional
  public void create_recordCreated() throws ResourceGoneException, ResourceNotFoundException {

    UUID collEventUUID = createWithRepository(CollectingEventTestFixture.newEventDto(), eventRepository::onCreate);

    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample()),
      Map.of(
        "collectingEvent", JsonApiDocument.ResourceIdentifier.builder().id(collEventUUID).type(CollectingEventDto.TYPENAME).build(),
        "preparedBy", List.of(JsonApiDocument.ResourceIdentifier.builder().id(MaterialSampleTestFixture.PREPARED_BY).type("person").build())
      )
    );

    UUID matSampleUuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));
    MaterialSampleDto result = materialSampleRepository.getOne(matSampleUuid, "include=collectingEvent,preparedBy").getDto();

    assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
    assertEquals(collEventUUID, result.getCollectingEvent().getUuid());
    assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().getFirst().getId());
    assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = { "notAAFC:user" })
  @Transactional
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    MaterialSample testMaterialSample = MaterialSampleFactory.newMaterialSample()
        .group(MaterialSampleTestFixture.GROUP).createdBy("dina").build();

    serviceTransactionWrapper.execute((ms) -> materialSampleService.create(ms), testMaterialSample);

    MaterialSampleDto retrievedMaterialSample = materialSampleRepository
        .getOne(testMaterialSample.getUuid(), "").getDto();

    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedMaterialSample.getJsonApiId(), MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedMaterialSample)
    );

    assertThrows(AccessDeniedException.class,
        () -> materialSampleRepository.onUpdate(docToUpdate, retrievedMaterialSample.getJsonApiId()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = { "aafc:user" })
  @Transactional
  public void when_deleteAsUserFromMaterialSampleGroup_MaterialSampleDeleted()
      throws ResourceGoneException, ResourceNotFoundException {

    UUID collEventUUID = createWithRepository(CollectingEventTestFixture.newEventDto(), eventRepository::onCreate);

    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample()),
      Map.of("collectingEvent", JsonApiDocument.ResourceIdentifier.builder()
        .id(collEventUUID).type(CollectingEventDto.TYPENAME).build())
    );
    UUID matSampleUuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));

    MaterialSampleDto result = materialSampleRepository.getOne(matSampleUuid, "").getDto();

    assertNotNull(result.getUuid());
    materialSampleRepository.onDelete(result.getUuid());

    assertThrows(ResourceGoneException.class, () -> materialSampleRepository.getOne(matSampleUuid, ""));
  }

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

  @Test
  @WithMockKeycloakUser(groupRole = { OrganismTestFixture.GROUP + ":user" })
  @Transactional
  public void updateMaterialSample_WithOrganism_accepted()
      throws MalformedURLException, ResourceGoneException, ResourceNotFoundException {

    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    UUID organismUUID = createWithRepository(organismDto, organismRepository::onCreate);

    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample()),
      Map.of("organism", List.of(JsonApiDocument.ResourceIdentifier.builder()
        .id(organismUUID).type(OrganismDto.TYPENAME).build()))
    );
    UUID matSampleUuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));

    MaterialSampleDto result = materialSampleRepository.getOne(matSampleUuid, "include=organism").getDto();
    assertEquals(organismUUID, result.getOrganism().getFirst().getUuid());

    // swap the organism for a new one
    OrganismDto organismDto2 = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    UUID organism2UUID = createWithRepository(organismDto2, organismRepository::onCreate);

    JsonApiDocument materialSampleToUpdate = JsonApiDocuments.createJsonApiDocument(
      matSampleUuid, MaterialSampleDto.TYPENAME,
      Map.of(),
      Map.of("organism", List.of(JsonApiDocument.ResourceIdentifier.builder()
        .id(organism2UUID).type(OrganismDto.TYPENAME).build()))
    );

    materialSampleRepository.onUpdate(materialSampleToUpdate, matSampleUuid);

    result = materialSampleRepository.getOne(matSampleUuid, "include=organism").getDto();
    assertEquals(organism2UUID, result.getOrganism().getFirst().getUuid());

    // we should be able to delete the first one since it's not used anymore
    organismRepository.onDelete(organismUUID);
  }

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
    newAttribute = serviceTransactionWrapper.executeWithParam( (p) ->
      collManagedAttributeRepo.create(p, null).getDto(), docToCreate);

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
    materialSampleRepository.onDelete(matSampleId);

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
    newAttribute = serviceTransactionWrapper.executeWithParam( (p) ->
      collManagedAttributeRepo.create(p, null).getDto(), docToCreate);

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
    materialSampleRepository.onDelete(matSampleId);

    // can't delete managed attribute for now since the check for key in use is using a fresh transaction
  }

}
