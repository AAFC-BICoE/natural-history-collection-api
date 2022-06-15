package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.entity.ManagedAttribute.ManagedAttributeType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.PersistenceException;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleCRUDIT extends CollectionModuleBaseIT {

  private final List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

  private static final String dwcCatalogNumber = "S-4313";
  private static final String[] dwcOtherCatalogNumbers = new String[]{"A-1111", "B-2222"};
  private static final String expectedCreatedBy = "dina-save";
  private static final String sampleMaterialName = "lake water sample";
  private static final UUID preparedBy = UUID.randomUUID();
  private static final String preparationExpectedCreatedBy = "preparation-dina-save";
  private static final String preparationExpectedGroup = "dina-group-save";
  private static final String preparationExpectedName = "isolate lake water sample";
  private static final String materialSampleTypeExpectedCreatedBy = "material-sample-type-save";
  private static final String materialSampleTypeExpectedName = "liquid lake water sample";
  private static final String storageUnitExpectedCreatedBy = "su-createdBy";
  private static final String storageUnitExpectedGroup = "su-dina";
  private static final String storageUnitExpectedName = "su-name";
  private static final String expectedPreparationRemarks = "this is a remark on the preparation";
  private static final String expectDwcDegreeOfEstablishment = "established";
  private static final LocalDate preparationDate = LocalDate.now();
  private static final String materialSampleUniqueName = "unique-test";
  private static final String ORGANISM_LIFESTAGE = "lifestage-test";
  private PreparationType preparationType;
  private MaterialSample.MaterialSampleType materialSampleType;
  private MaterialSample materialSample;
  private StorageUnit storageUnit;
  private Collection collection;


  @BeforeEach
  void setup() {
    Institution institution = InstitutionFixture.newInstitutionEntity().build();
    service.save(institution);

    storageUnit = StorageUnitFactory.newStorageUnit()
        .createdBy(storageUnitExpectedCreatedBy)
        .group(storageUnitExpectedGroup)
        .name(storageUnitExpectedName)
        .build();
    storageUnitService.create(storageUnit);

    preparationType = PreparationTypeFactory.newPreparationType()
        .createdBy(preparationExpectedCreatedBy)
        .group(preparationExpectedGroup)
        .name(preparationExpectedName)
        .build();
    preparationTypeService.create(preparationType);

    materialSampleType = MaterialSample.MaterialSampleType.WHOLE_ORGANISM;

    collection = collectionService.create(Collection.builder()
      .code(RandomStringUtils.randomAlphabetic(4))
      .name(RandomStringUtils.randomAlphabetic(3))
      .institution(institution)
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .group(RandomStringUtils.randomAlphabetic(4))
      .build());

    materialSample = MaterialSampleFactory.newMaterialSample()
        .dwcCatalogNumber(dwcCatalogNumber)
        .dwcOtherCatalogNumbers(dwcOtherCatalogNumbers)
        .createdBy(expectedCreatedBy)
        .attachment(attachmentIdentifiers)
        .preparedBy(preparedBy)
        .materialSampleName(sampleMaterialName)
        .preparationDate(preparationDate)
        .preparationType(preparationType)
        .materialSampleType(materialSampleType)
        .storageUnit(storageUnit)
        .preparationRemarks(expectedPreparationRemarks)
        .dwcDegreeOfEstablishment(expectDwcDegreeOfEstablishment)
        .collection(collection)
        .build();
    materialSampleService.create(materialSample);
  }

  @Test
  public void testCreate() {

    assertNotNull(preparationType.getId());
    assertNotNull(materialSample.getId());

    assertEquals(dwcCatalogNumber, materialSample.getDwcCatalogNumber());
    assertEquals(dwcOtherCatalogNumbers, materialSample.getDwcOtherCatalogNumbers());
    assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
    assertEquals(attachmentIdentifiers, materialSample.getAttachment());
    assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
    assertEquals(expectedPreparationRemarks, materialSample.getPreparationRemarks());
    assertEquals(expectDwcDegreeOfEstablishment, materialSample.getDwcDegreeOfEstablishment());

    assertEquals(preparationType.getId(), materialSample.getPreparationType().getId());
    assertEquals(preparationExpectedCreatedBy, materialSample.getPreparationType().getCreatedBy());
    assertEquals(preparationExpectedGroup, materialSample.getPreparationType().getGroup());
    assertEquals(preparationExpectedName, materialSample.getPreparationType().getName());

    assertEquals(materialSampleType, materialSample.getMaterialSampleType());

    assertEquals(preparedBy, materialSample.getPreparedBy());
    assertEquals(preparationDate, materialSample.getPreparationDate());

    assertEquals(storageUnit.getId(), materialSample.getStorageUnit().getId());
    assertEquals(storageUnitExpectedCreatedBy, materialSample.getStorageUnit().getCreatedBy());
    assertEquals(storageUnitExpectedGroup, materialSample.getStorageUnit().getGroup());
    assertEquals(storageUnitExpectedName, materialSample.getStorageUnit().getName());
    assertEquals(collection.getUuid(), materialSample.getCollection().getUuid());
  }

  @Test
  public void testFind() {

    MaterialSample fetchedMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);

    assertEquals(materialSample.getId(), fetchedMaterialSample.getId());
    assertEquals(materialSample.getBarcode(), fetchedMaterialSample.getBarcode());
    assertEquals(dwcCatalogNumber, fetchedMaterialSample.getDwcCatalogNumber());
    assertEquals(dwcOtherCatalogNumbers, fetchedMaterialSample.getDwcOtherCatalogNumbers());
    assertEquals(expectedCreatedBy, fetchedMaterialSample.getCreatedBy());
    assertEquals(attachmentIdentifiers, fetchedMaterialSample.getAttachment());
    assertEquals(sampleMaterialName, fetchedMaterialSample.getMaterialSampleName());
    assertEquals(expectedPreparationRemarks, fetchedMaterialSample.getPreparationRemarks());
    assertEquals(expectDwcDegreeOfEstablishment, fetchedMaterialSample.getDwcDegreeOfEstablishment());

    assertEquals(preparationType.getId(), fetchedMaterialSample.getPreparationType().getId());
    assertEquals(preparationExpectedCreatedBy, fetchedMaterialSample.getPreparationType().getCreatedBy());
    assertEquals(preparationExpectedGroup, fetchedMaterialSample.getPreparationType().getGroup());
    assertEquals(preparationExpectedName, fetchedMaterialSample.getPreparationType().getName());

    assertEquals(materialSampleType, fetchedMaterialSample.getMaterialSampleType());

    assertEquals(storageUnit.getId(), fetchedMaterialSample.getStorageUnit().getId());
    assertEquals(storageUnitExpectedCreatedBy, fetchedMaterialSample.getStorageUnit().getCreatedBy());
    assertEquals(storageUnitExpectedGroup, fetchedMaterialSample.getStorageUnit().getGroup());
    assertEquals(storageUnitExpectedName, fetchedMaterialSample.getStorageUnit().getName());

    assertEquals(preparedBy, fetchedMaterialSample.getPreparedBy());
    assertEquals(preparationDate, fetchedMaterialSample.getPreparationDate());
    assertEquals(collection.getUuid(), fetchedMaterialSample.getCollection().getUuid());
  }

  @Test
  public void testParentChildRelationship() {
    MaterialSample parent = MaterialSampleFactory.newMaterialSample()
        .dwcCatalogNumber("parent-" + dwcCatalogNumber)
        .createdBy("parent-" + expectedCreatedBy)
        .attachment(attachmentIdentifiers)
        .materialSampleName("parent-" + sampleMaterialName)
        .preparationType(preparationType)
        .build();

    parent = materialSampleService.create(parent);
    MaterialSampleParent msParent = materialSampleService.getReferenceByNaturalId(MaterialSampleParent.class, parent.getUuid());

    MaterialSample child = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber("child-" + dwcCatalogNumber)
            .createdBy("child-" + expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName("child-" + sampleMaterialName)
            .preparationType(preparationType)
            .parentMaterialSample(msParent)
            .build();

    materialSampleService.create(child);

    MaterialSample fetchedParent = materialSampleService.findOne(parent.getUuid(), MaterialSample.class);

    assertEquals(fetchedParent.getUuid(), child.getParentMaterialSample().getUuid());
    assertEquals(1, fetchedParent.getMaterialSampleChildren().size());
    assertEquals(child.getUuid(), fetchedParent.getMaterialSampleChildren().get(0).getUuid());
  }

  @Test
  public void testOrganismRelationship() {
    List<Determination> determinations = new ArrayList<>();
    List<Organism> organisms = new ArrayList<>();

    Determination determination = DeterminationFactory.newDetermination()
        .verbatimScientificName("verbatimScientificName")
        .isPrimary(false)
        .build();
    determinations.add(determination);

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();
    organisms.add(organismService.createAndFlush(organism));
    assertNotNull(organism.getUuid());

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
        .build();

    materialSampleService.createAndFlush(materialSample);

    materialSample.setOrganism(organisms);
    materialSampleService.update(materialSample);

    // Detach the entity to force a load from the database
    materialSampleService.detach(materialSample);

    MaterialSample freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    assertEquals(organism.getUuid(), freshMaterialSample.getOrganism().get(0).getUuid());
    assertEquals(determination.getVerbatimScientificName(), freshMaterialSample.getOrganism().get(0).getDetermination().get(0).getVerbatimScientificName());

    // Try updating the organism. Add a new determination to the organism.
    Determination determination2 = DeterminationFactory.newDetermination()
        .verbatimScientificName("secondVerbatimScientificName")
        .isPrimary(false)
        .build();
    determinations.add(determination2);

    organism.setLifeStage(ORGANISM_LIFESTAGE);
    organism.setDetermination(determinations);
    organismService.update(organism);

    freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    assertEquals(ORGANISM_LIFESTAGE, freshMaterialSample.getOrganism().get(0).getLifeStage());
    assertEquals(determination2.getVerbatimScientificName(), freshMaterialSample.getOrganism().get(0).getDetermination().get(1).getVerbatimScientificName());

    // Try adding a new organism to the material sample.
    Determination determination3 = DeterminationFactory.newDetermination()
      .verbatimScientificName("thirdVerbatimScientificName")
      .isPrimary(true)
      .build();

    Organism organism2 = OrganismEntityFactory.newOrganism()
      .determination(List.of(determination3))
      .build();
    organisms.add(organismService.createAndFlush(organism2));
    materialSample.setOrganism(organisms);
    materialSampleService.update(materialSample);

    freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    assertEquals(determination3.getVerbatimScientificName(), freshMaterialSample.getOrganism().get(1).getDetermination().get(0).getVerbatimScientificName());

    // Try deleting the material sample.
    materialSampleService.delete(freshMaterialSample);

    // Does the organism still exist?
    Organism freshOrganism = organismService.findOne(organism2.getUuid(), Organism.class);
    assertNotNull(freshOrganism);
  }

  @Test
  void validate_WhenValidStringType() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(null)
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    materialSample.setManagedAttributes(Map.of(testManagedAttribute.getKey(), "anything"));
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(null)
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
        .managedAttributeType(ManagedAttributeType.INTEGER)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    materialSample.setManagedAttributes(Map.of(testManagedAttribute.getKey(), "1.2"));

    assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
  }

  @Test
  void assignedValueContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    materialSample.setManagedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]));
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));
  }

  @Test
  void assignedValueNotContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    materialSample.setManagedAttributes(Map.of(testManagedAttribute.getKey(), "val3"));
    assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
  }

  @Test
  void assignManagedAttribute_onCollectingEventAttribute_Exception() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory
        .newCollectionManagedAttribute().acceptedValues(new String[] { "val1", "val2" })
        .managedAttributeComponent(
            CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT).build();

    collectionManagedAttributeService.create(testManagedAttribute);

    materialSample.setManagedAttributes(Map.of(testManagedAttribute.getKey(), "val1"));
    assertThrows(ValidationException.class, () -> materialSampleService.update(materialSample));
  }

  @Test
  void nestedStructureValidation_Exception() {
    HostOrganism hostOrganism = HostOrganism.builder()
      .name(RandomStringUtils.randomAlphanumeric(151))
      .remarks("host remark")
      .build();
    
    materialSample.setHostOrganism(hostOrganism);
    
    assertEquals(151, materialSample.getHostOrganism().getName().length());

    assertThrows(ValidationException.class, 
    () -> materialSampleService.update(materialSample));
  }

  @Test
  void materialSampleNameDuplicatedNames_allowDuplicatesFalse_Exception() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName.toLowerCase())
        .collection(collection)
        .allowDuplicateName(false)
        .build();

    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with the same name (with allow duplicates set to false)
    // should throw an error. This test also ensures that the index is case sensitive.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName.toUpperCase())
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    assertThrows(PersistenceException.class, () -> materialSampleService.create(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNamesDifferentCollections_allowDuplicatesFalse_createRecord() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName.toLowerCase())
        .collection(collection)
        .allowDuplicateName(false)
        .build();

    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with the same name (with allow duplicates
    // set to false) in a DIFFERENT collection should be allowed. The record should be
    // created with no issues.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName.toLowerCase())
        .collection(CollectionFactory.newCollection().build())
        .allowDuplicateName(false)
        .build();
    assertDoesNotThrow(() -> materialSampleService.create(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNames_allowDuplicatesTrue_createRecord() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with the same name (with allow duplicates set to true)
    // should not cause any issues since the allow duplicate boolean activated.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(true)
        .build();
    assertDoesNotThrow(() -> materialSampleService.create(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNames_allowDuplicatesTrueThenFalse_Exception() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(true)
        .build();
    materialSampleService.create(materialSampleDuplicate1);

    // Since the name has been already used above, it should trigger an exception, the allow duplicated
    // name being true above does not exclude it from the check.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    assertThrows(PersistenceException.class, () -> materialSampleService.create(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNames_updateAllowDuplicatesFalse_Exception() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with the same name (with allow duplicates set to true)
    // should not cause any issues since the allow duplicate boolean activated.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(true)
        .build();
    materialSampleService.create(materialSampleDuplicate2);

    // Now update materialSampleDuplicate2 to not allow duplicates, should fail.
    materialSampleDuplicate2.setAllowDuplicateName(false);
    assertThrows(PersistenceException.class, () -> materialSampleService.update(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNames_changeNameToExistingName_Exception() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with two different names.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName("materialSampleName2")
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate2);

    // Now update materialSampleDuplicate2 to materialSampleDuplicate1 name, should fail.
    materialSampleDuplicate2.setMaterialSampleName(materialSampleUniqueName);
    assertThrows(PersistenceException.class, () -> materialSampleService.update(materialSampleDuplicate2));
  }

  @Test
  void materialSampleNameDuplicatedNames_changeCollectionWithSameName_Exception() {
    MaterialSample materialSampleDuplicate1 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collection)
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate1);

    // Creating another material sample with a different collection.
    MaterialSample materialSampleDuplicate2 = MaterialSampleFactory.newMaterialSample()
        .materialSampleName(materialSampleUniqueName)
        .collection(collectionService.create(CollectionFactory.newCollection().build()))
        .allowDuplicateName(false)
        .build();
    materialSampleService.create(materialSampleDuplicate2);

    // Now update materialSampleDuplicate2 to use the same collection as materialSampleDuplicate1, should fail.
    materialSampleDuplicate2.setCollection(collection);
    assertThrows(PersistenceException.class, () -> materialSampleService.update(materialSampleDuplicate2));
  }

  @Test
  void updateMaterialSample_WhenAssociatedWithSelf_Exception() {

    Association association = Association.builder()
      .associationType(RandomStringUtils.randomAlphabetic(4))
      .build();
    association.setAssociatedSample(materialSample);
    association.setSample(materialSample);

    materialSample.setAssociations(List.of(association));

    assertThrows(ValidationException.class, () -> materialSampleService.update(materialSample));
  }

}
