package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleTypeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.entity.ManagedAttribute.ManagedAttributeType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
  private static final String materialSampleTypeExpectedGroup = "material-sample-type-group-save";
  private static final String materialSampleTypeExpectedName = "liquid lake water sample";
  private static final String storageUnitExpectedCreatedBy = "su-createdBy";
  private static final String storageUnitExpectedGroup = "su-dina";
  private static final String storageUnitExpectedName = "su-name";
  private static final String expectedPreparationRemarks = "this is a remark on the preparation";
  private static final String expectDwcDegreeOfEstablishment = "established";
  private static final LocalDate preparationDate = LocalDate.now();
  private PreparationType preparationType;
  private MaterialSampleType materialSampleType;
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

    materialSampleType = MaterialSampleTypeFactory.newMaterialSampleType()
        .createdBy(materialSampleTypeExpectedCreatedBy)
        .name(materialSampleTypeExpectedName)
        .build();
    materialSampleTypeService.create(materialSampleType);

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

    assertEquals(materialSampleType.getId(), materialSample.getMaterialSampleType().getId());
    assertEquals(materialSampleTypeExpectedCreatedBy, materialSample.getMaterialSampleType().getCreatedBy());
    assertEquals(materialSampleTypeExpectedName, materialSample.getMaterialSampleType().getName());

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

    assertEquals(materialSampleType.getId(), fetchedMaterialSample.getMaterialSampleType().getId());
    assertEquals(materialSampleTypeExpectedCreatedBy, fetchedMaterialSample.getMaterialSampleType().getCreatedBy());
    assertEquals(materialSampleTypeExpectedName, fetchedMaterialSample.getMaterialSampleType().getName());

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

    MaterialSample child = MaterialSampleFactory.newMaterialSample()
        .dwcCatalogNumber("child-" + dwcCatalogNumber)
        .createdBy("child-" + expectedCreatedBy)
        .attachment(attachmentIdentifiers)
        .materialSampleName("child-" + sampleMaterialName)
        .preparationType(preparationType)
        .parentMaterialSample(parent)
        .build();

    parent = materialSampleService.create(parent);
    child.setParentMaterialSample(parent);
    materialSampleService.create(child);

    MaterialSample fetchedParent = materialSampleService.findOne(parent.getUuid(), MaterialSample.class);

    assertEquals(fetchedParent.getUuid(), child.getParentMaterialSample().getUuid());
    assertEquals(1, fetchedParent.getMaterialSampleChildren().size());
    assertEquals(child.getUuid(), fetchedParent.getMaterialSampleChildren().get(0).getUuid());
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
  void determinedOnIsInFuture_Exception() {
    Determination determination = Determination.builder()
      .verbatimScientificName("verbatimScientificName")
      .determinedOn(LocalDate.now().plusDays(2))
      .build();

    List<Determination> determinations = List.of(determination);
    
    materialSample.setDetermination(determinations);
    
    assertThrows(ValidationException.class, 
      () -> materialSampleService.update(materialSample));
  }

}
