package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismFactory;

import java.util.Collections;

public class OrganismFixture {

  public static Organism newOrganism(Determination determination) {
    return OrganismFactory.newOrganism()
        .determination(Collections.singletonList(determination))
        .lifeStage("larva")
        .sex("female")
        .substrate("organism subtrate")
        .remarks("remark")
        .build();
  }
}
