package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
