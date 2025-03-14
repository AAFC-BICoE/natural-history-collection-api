package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.validation.ValidationException;

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

    assertTrue(organism.getDetermination().get(0).getIsPrimary());
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
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{"val1", "val2"})
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.ORGANISM)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Organism organism = OrganismEntityFactory.newOrganism()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]))
      .build();

    assertDoesNotThrow(() -> organismService.create(organism));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.ORGANISM)
      .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Organism organism = OrganismEntityFactory.newOrganism()
      .managedAttributes(Map.of(testManagedAttribute.getKey(), "1.2"))
      .build();

    assertThrows(ValidationException.class, () ->  organismService.create(organism));
  }

}
