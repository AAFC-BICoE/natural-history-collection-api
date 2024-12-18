package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.Determination;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;

public class OrganismTestFixture {

  public static final String GROUP = "aafc";

  public static OrganismDto newOrganism(Determination determination) {
    return OrganismDto.builder()
        .group(GROUP)
        .createdBy(RandomStringUtils.randomAlphabetic(7))
        .determination(Collections.singletonList(determination))
        .lifeStage("larva")
        .sex("female")
        .remarks("remark")
        .dwcVernacularName(RandomStringUtils.randomAlphabetic(12))
        .build();
  }
}
