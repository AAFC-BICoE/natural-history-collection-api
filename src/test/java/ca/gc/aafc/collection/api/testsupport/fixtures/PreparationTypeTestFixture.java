package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;

public class PreparationTypeTestFixture {

  private static final String GROUP = "aafc";

  public static PreparationTypeDto newPreparationType() {
    PreparationTypeDto preparationTypeDto = new PreparationTypeDto();
    preparationTypeDto.setName(RandomStringUtils.randomAlphabetic(5));
    preparationTypeDto.setGroup(GROUP);
    return preparationTypeDto;
  }
  
}
