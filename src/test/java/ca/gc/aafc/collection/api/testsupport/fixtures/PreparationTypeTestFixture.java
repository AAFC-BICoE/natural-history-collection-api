package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;

public class PreparationTypeTestFixture {

  private static final String GROUP = "aafc";
  private static final String NAME = "preparation process definition";

  public static PreparationTypeDto newPreparationType() {
    PreparationTypeDto preparationTypeDto = new PreparationTypeDto();
    preparationTypeDto.setName(NAME);
    preparationTypeDto.setGroup(GROUP);
    return preparationTypeDto;
  }
  
}
