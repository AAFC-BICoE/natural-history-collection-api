package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionControlledVocabularyItemFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ValidationException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganismServiceIT extends CollectionModuleBaseIT {

  @Test
  void organismDetermination_onNullIsPrimary_isPrimarySet() {
    Determination determination = DeterminationFactory.newDetermination()
        .isPrimary(null) // force null
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(List.of(determination))
        .build();
    organismService.create(organism);

    assertTrue(organism.getDetermination().getFirst().getIsPrimary());
  }

  @Test
  void organismDetermination_extractClassification_expectedResultReturned() {
    Determination determination = DeterminationFactory.newDetermination()
      .scientificNameDetails(Determination.ScientificNameSourceDetails.builder()
        .sourceUrl(URI.create("https://www.google.com").toString())
        .recordedOn(LocalDate.now().minusDays(1))
        .classificationPath("a|b|c")
        .classificationRanks("family|genus|c")
        .build())
      .isPrimary(null) // force null
      .build();

    Map<String, String> a = organismService.extractClassification(determination);

    assertTrue(a.containsKey("family"));
  }

  @Test
  void assignedValueContainedInAcceptedValues_validationPasses() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.ORGANISM.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Organism organism = OrganismEntityFactory.newOrganism()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]))
      .build();

    assertDoesNotThrow(() -> organismService.create(organism));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(null)
        .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER)
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.ORGANISM.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();

    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Organism organism = OrganismEntityFactory.newOrganism()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "1.2"))
      .build();

    assertThrows(ValidationException.class, () ->  organismService.create(organism));
  }

  @Test
  void validateDetermination_AssignedValueNotContainedInAcceptedValues_validationPasses() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.DETERMINATION.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

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
  void validateDetermination_AssignManagedAttribute_onWronDinaComponent_Exception() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "val1"))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
      .determination(new ArrayList<>(List.of(determination)))
      .build();

    assertThrows(ValidationException.class, () -> organismService.update(organism));
  }


  @Test
  void validateDetermination_WhenValidStringType() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(null)
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.DETERMINATION.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .isPrimary(true)
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "anything"))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
      .determination(new ArrayList<>(List.of(determination)))
      .build();

    assertDoesNotThrow(() -> organismService.create(organism));

    // Clean up
    organismService.delete(organism);
  }

  @Test
  void validateDetermination_AssignedValueContainedInAcceptedValues_validationPasses() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.DETERMINATION.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Determination determination = DeterminationFactory.newDetermination()
      .isPrimary(true)
      .managedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]))
      .build();

    Organism organism = OrganismEntityFactory.newOrganism()
      .determination(new ArrayList<>(List.of(determination)))
      .build();

    assertDoesNotThrow(() -> organismService.create(organism));

    // Clean up
    organismService.delete(organism);
  }
}
