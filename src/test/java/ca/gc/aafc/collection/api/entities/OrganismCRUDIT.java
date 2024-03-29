package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrganismCRUDIT extends CollectionModuleBaseIT {

  @Test
  void create() {
    Organism organism = OrganismEntityFactory.newOrganism()
        .build();
    organismService.createAndFlush(organism);

    Organism result = organismService.findOne(organism.getUuid(), Organism.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(OrganismEntityFactory.GROUP, result.getGroup());

    //cleanup
    organismService.delete(organism);
  }

  @Test
  void determinedOnIsInFuture_Exception() {
    Determination determination = Determination.builder()
        .verbatimScientificName("verbatimScientificName")
        .determinedOn(LocalDate.now().plusDays(2))
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(List.of(determination))
        .build();

    assertThrows(ValidationException.class,
        () -> organismService.createAndFlush(organism));
  }

  @Test
  void validateDetermination_AssignedValueNotContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .managedAttributes(Map.of(testManagedAttribute.getKey(), "val3"))
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(new ArrayList<>(List.of(determination)))
        .build();
    assertThrows(ValidationException.class, () ->  organismService.createAndFlush(organism));
  }

  @Test
  void onNew_assertDefaultIsSynonymIsFalse_Passes() {
    Determination.ScientificNameSourceDetails scientificNameSourceDetails =
        Determination.ScientificNameSourceDetails.builder().build();
    assertFalse(scientificNameSourceDetails.getIsSynonym());
  }

  @Test
  void updateOrganism_WhenOnlyDeterminationIsPrimaryIsFalse_Passes() {
    Determination determination = Determination.builder()
        .verbatimScientificName("verbatimScientificName")
        .isPrimary(false)
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(new ArrayList<>(List.of(determination)))
        .build();

    assertDoesNotThrow(() -> organismService.createAndFlush(organism));
    Organism fetchedOrganism = organismService.findOne(organism.getUuid(), Organism.class);
    assertTrue(fetchedOrganism.getDetermination().get(0).getIsPrimary());
  }

  @Test
  void validateDetermination_WhenValidStringType() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(null)
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION)
        .build();
    collectionManagedAttributeService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .isPrimary(true)
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "anything"))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(new ArrayList<>(List.of(determination)))
        .build();

    assertDoesNotThrow(() -> organismService.create(organism));

    // Clean up
    collectionManagedAttributeService.delete(testManagedAttribute);
    organismService.delete(organism);
  }

  @Test
  void validateDetermination_AssignedValueContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION)
        .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .isPrimary(true)
      .managedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
      .determination(new ArrayList<>(List.of(determination)))
      .build();

    assertDoesNotThrow(() -> organismService.create(organism));

    // Clean up
    collectionManagedAttributeService.delete(testManagedAttribute);
    organismService.delete(organism);
  }

  @Test
  void validateDetermination_AssignManagedAttribute_onCollectingEventAttribute_Exception() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory
        .newCollectionManagedAttribute().acceptedValues(new String[] { "val1", "val2" })
        .managedAttributeComponent(
            CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT).build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "val1"))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
      .determination(new ArrayList<>(List.of(determination)))
      .build();

    assertThrows(ValidationException.class, () -> organismService.update(organism));

    // Clean up
    collectionManagedAttributeService.delete(testManagedAttribute);
  }

  @Test
  void targetOrganism_multipleTargetsSameMaterialSample_Exception() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
        .isPrimary(false)
        .isFiledAs(false)
        .verbatimScientificName("verbatimScientificName")
        .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism2));

    materialSample.setOrganism(organisms);

    // unique constraint will trigger an exception
    assertThrows(PersistenceException.class, () -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganism_oneTargetOrganism_noExceptions() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
        .isPrimary(false)
        .isFiledAs(false)
        .verbatimScientificName("verbatimScientificName")
        .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
        .isTarget(false)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganism_noTargetOrganism_noExceptions() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
            .isPrimary(false)
            .isFiledAs(false)
            .verbatimScientificName("verbatimScientificName")
            .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganismNotUsed_startUsingTargetOrganism_SaveSuccess() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
            .isPrimary(false)
            .isFiledAs(false)
            .verbatimScientificName("verbatimScientificName")
            .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    materialSampleService.update(materialSample);

    // now start making use of isTarget
    organism1.setIsTarget(true);
    organism2.setIsTarget(false);

    materialSampleService.update(materialSample);

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }
}
