package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
