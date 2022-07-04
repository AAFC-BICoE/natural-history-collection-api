package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import org.apache.commons.lang3.RandomStringUtils;

public class PreparationMethodTestFixture {

  public static final String GROUP = "aafc";

  public static PreparationMethodDto newPreparationMethod() {
    PreparationMethodDto preparationMethodDto = new PreparationMethodDto();
    preparationMethodDto.setName(RandomStringUtils.randomAlphabetic(5));
    preparationMethodDto.setMultilingualDescription(MultilingualDescriptionFactory.newMultilingualDescription());
    preparationMethodDto.setGroup(GROUP);
    return preparationMethodDto;
  }
}
