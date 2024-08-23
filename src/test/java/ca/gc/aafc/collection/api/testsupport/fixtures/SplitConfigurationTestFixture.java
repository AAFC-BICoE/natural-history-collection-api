package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.SplitConfigurationDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;

public class SplitConfigurationTestFixture {

  public static final String GROUP = "aafc";

  public static SplitConfigurationDto newSplitConfiguration() {
    SplitConfigurationDto splitConfigurationDto = new SplitConfigurationDto();
    splitConfigurationDto.setName(RandomStringUtils.randomAlphabetic(5));
    splitConfigurationDto.setGroup(GROUP);
    splitConfigurationDto.setStrategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED);
    splitConfigurationDto.setSeparator("-");
    splitConfigurationDto.setConditionalOnMaterialSampleTypes(
      new MaterialSample.MaterialSampleType[] {
        MaterialSample.MaterialSampleType.WHOLE_ORGANISM,
        MaterialSample.MaterialSampleType.CULTURE_STRAIN});
    splitConfigurationDto.setCharacterType(MaterialSampleNameGeneration.CharacterType.UPPER_LETTER);

    return splitConfigurationDto;
  }
}
