package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganismServiceIT extends CollectionModuleBaseIT {


  @Test
  void organismDetermination_onNullIsPrimary_isPrimarySet() {
    Determination determination = Determination.builder()
        .isPrimary(null) // force null
        .verbatimScientificName("verbatimScientificName A")
        .scientificName("scientificName A")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(List.of(determination))
        .build();
    organismService.create(organism);

    assertTrue(organism.getDetermination().get(0).getIsPrimary());
  }

}
