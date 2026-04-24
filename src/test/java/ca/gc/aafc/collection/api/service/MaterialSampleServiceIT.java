package ca.gc.aafc.collection.api.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample.MaterialSampleType;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.testsupport.TransactionTestingHelper;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


public class MaterialSampleServiceIT extends CollectionModuleBaseIT {

  @Inject
  private BaseDAO baseDAO;

  @Inject
  private TransactionTestingHelper transactionTestingHelper;

  @Test
  void onFindOne_lazyLoadedRelationship() {
    Collection c = CollectionFactory.newCollection().build();
    collectionService.create(c);
    MaterialSample ms = MaterialSampleFactory.newMaterialSample()
      .collection(c)
      .build();
    materialSampleService.create(ms);
    materialSampleService.flush();

    //remove from cache to force reload from the database
    materialSampleService.detach(ms);
    collectionService.detach(c);

    MaterialSample freshMs = materialSampleService.findOne(ms.getUuid(), MaterialSample.class);

    assertFalse(baseDAO.isLoaded(freshMs, "collection"));
  }

  @Test
  void create_onLinkingToAnExistingProject_secondMaterialSampleSaved() {
    Project p = ProjectFactory.newProject().build();
    projectService.create(p);

    MaterialSample sample1 = MaterialSampleFactory.newMaterialSample()
        .projects(List.of(p))
        .build();

    materialSampleService.createAndFlush(sample1);

    MaterialSample sample2 = MaterialSampleFactory.newMaterialSample()
        .projects(List.of(p))
        .build();

    materialSampleService.createAndFlush(sample2);
  }

  @Test
  public void hierarchy_onSetHierarchy_hierarchyLoaded() throws JsonProcessingException {

    Determination determination = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName")
            .isPrimary(false)
            .build();
    Organism organism = OrganismEntityFactory.newOrganism()
            .isTarget(true)
            .determination(List.of(determination))
            .build();

    // we need to create the entities in another transaction so that MyBatis can see it.
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism));
    assertNotNull(organism.getUuid());

    MaterialSample parentMaterialSample = MaterialSampleFactory.newMaterialSample()
              .organism(List.of(organism))
              .build();
    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(parentMaterialSample));

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();

    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(materialSample));

    MaterialSample freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    materialSampleService.setHierarchy(freshMaterialSample);
    assertEquals(2, freshMaterialSample.getHierarchy().size());
    assertNotNull(freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination());

    materialSampleService.setTargetOrganismPrimaryScientificName(parentMaterialSample);
    assertEquals("verbatimScientificName", parentMaterialSample.getTargetOrganismPrimaryScientificName());

    //cleanup
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(materialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(parentMaterialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism));
  }

  @Test
  public void hierarchy_onHierarchyNoTargetOrganism_hierarchyLoaded() throws JsonProcessingException {

    Determination determination = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName")
            .isPrimary(true)
            .build();
    Organism organism = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    Determination determination2 = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName2")
            .isPrimary(true)
            .build();
    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination2))
            .build();

    // we need to create the entities in another transaction so that MyBatis can see it.
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism));
    assertNotNull(organism.getUuid());
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism2));
    assertNotNull(organism2.getUuid());

    MaterialSample parentMaterialSample = MaterialSampleFactory.newMaterialSample()
            .organism(List.of(organism, organism2))
            .build();
    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(parentMaterialSample));

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();

    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(materialSample));

    MaterialSample freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    materialSampleService.setHierarchy(freshMaterialSample);
    assertEquals(2, freshMaterialSample.getHierarchy().size());
    assertEquals(1, freshMaterialSample.getHierarchy().get(0).getRank());
    assertTrue(StringUtils.isNotBlank(freshMaterialSample.getHierarchy().get(0).getName()));
    assertNotNull(freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination());
    assertEquals(2, freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination().size());

    //cleanup
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(materialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(parentMaterialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism2));
  }

  @Test
  public void materialSampleChildren_onRequest_ordinalLoaded() {
    MaterialSample parentMaterialSample = MaterialSampleFactory.newMaterialSample()
            .build();
    materialSampleService.create(parentMaterialSample);

    MaterialSample materialSample1 = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();
    MaterialSample materialSample2 = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();

    materialSampleService.create(materialSample1);
    materialSampleService.create(materialSample2);


    MaterialSample loadedMaterialSample = materialSampleService.findOne(parentMaterialSample.getUuid(), MaterialSample.class);
    materialSampleService.setChildrenOrdinal(loadedMaterialSample);
    assertEquals(2, loadedMaterialSample.getMaterialSampleChildren().size());

    Set<Integer> expectedOrdinal = new HashSet<>(Set.of(0,1));
    assertTrue(expectedOrdinal.remove(loadedMaterialSample.getMaterialSampleChildren().get(0).getOrdinal()));
    assertTrue(expectedOrdinal.remove(loadedMaterialSample.getMaterialSampleChildren().get(1).getOrdinal()));
    // make sure we found the 2 numbers
    assertTrue(expectedOrdinal.isEmpty());
  }

  @Test
  public void materialSampleType_supportedEnums_ableToPersistAllTypes() {
    // Retrieve all supported MaterialSampleType enum values
    Stream.of(MaterialSampleType.values()).forEach((enumType) -> {
      // Create a new MaterialSample using the current enum value and persist it.
      MaterialSample persistMaterialSample = MaterialSampleFactory.newMaterialSample().materialSampleType(enumType)
          .build();
      materialSampleService.create(persistMaterialSample);
    });
  }

  @Test
  void validate_WhenValidStringType() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "anything"))
      .build();

    assertDoesNotThrow(() -> materialSampleService.update(materialSample));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
      .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "1.2"))
      .build();

    assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
  }

  @Test
  void assignedValueContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{"val1", "val2"})
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]))
      .build();

    assertDoesNotThrow(() -> materialSampleService.update(materialSample));
  }

  @Test
  void preparationManagedAttributes_validationApplied() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER)
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.PREPARATION)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .preparationManagedAttributes(Map.of(testManagedAttribute.getKey(), "7"))
      .build();

    assertDoesNotThrow(() -> materialSampleService.update(materialSample));

    materialSample.setPreparationManagedAttributes(Map.of(testManagedAttribute.getKey(), "abc"));
    assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
  }

  @Test
  void assignedValueNotContainedInAcceptedValues_Exception() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{"val1", "val2"})
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "val3"))
      .build();

    assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
  }

  @Test
  void assignManagedAttribute_onCollectingEventAttribute_Exception() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory
      .newCollectionManagedAttribute().acceptedValues(new String[] { "val1", "val2" })
      .managedAttributeComponent(
        CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT).build();

    collectionManagedAttributeService.create(testManagedAttribute);

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "val1"))
      .build();

    assertThrows(ValidationException.class, () -> materialSampleService.update(materialSample));
  }

  @Test
  void nestedStructureValidation_Exception() {
    HostOrganism hostOrganism = HostOrganism.builder()
      .name(RandomStringUtils.randomAlphanumeric(151))
      .remarks("host remark")
      .build();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
      .hostOrganism(hostOrganism)
      .build();

    assertEquals(151, materialSample.getHostOrganism().getName().length());

    assertThrows(ValidationException.class,
      () -> materialSampleService.update(materialSample));
  }
}
