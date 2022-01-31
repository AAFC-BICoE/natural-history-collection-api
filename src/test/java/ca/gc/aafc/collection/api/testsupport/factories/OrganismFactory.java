package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.Collections;
import java.util.List;

public class OrganismFactory implements TestableEntityFactory<Organism> {

  @Override
  public Organism getEntityInstance() {
    return newOrganism().build();
  }

  public static Organism.OrganismBuilder newOrganism() {
    return Organism.builder();
  }

  public static List<Organism> buildAsList(Organism.OrganismBuilder bldr) {
    return Collections.singletonList(bldr.build());
  }


}
