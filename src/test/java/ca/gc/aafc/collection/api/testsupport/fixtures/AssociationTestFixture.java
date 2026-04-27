package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.AssociationDto;

public class AssociationTestFixture {

  public static AssociationDto newAssociation() {
    return AssociationDto.builder()
      .associationType("host_of")
      .build();
  }
}
