package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.OrganismEntity;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.Collections;
import java.util.List;

public class OrganismEntityFactory implements TestableEntityFactory<OrganismEntity> {

  public static final String GROUP = "aafc";

  public static OrganismEntity.OrganismEntityBuilder newOrganism() {
    return OrganismEntity
        .builder()
        .group(GROUP)
        .createdBy("test user");
  }

  @Override
  public OrganismEntity getEntityInstance() {
    return newOrganism()
        .build();
  }

  public static List<OrganismEntity> buildAsList(OrganismEntity.OrganismEntityBuilder bldr) {
    return Collections.singletonList(bldr.build());
  }
}
