package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.MaterialSampleTypeDto;

public class MaterialSampleTypeTestFixture {
  
  public static MaterialSampleTypeDto newMaterialSampleType() {
    MaterialSampleTypeDto materialSampleTypeDto = new MaterialSampleTypeDto();
    materialSampleTypeDto.setCreatedBy(RandomStringUtils.randomAlphabetic(4));
    materialSampleTypeDto.setName(RandomStringUtils.randomAlphabetic(4));
    return materialSampleTypeDto;
  }

}
