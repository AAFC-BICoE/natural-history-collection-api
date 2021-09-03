package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.InstitutionDto;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

public final class InstitutionFixture {

  private InstitutionFixture() {
  }

  public static InstitutionDto newInstitution() {
    return InstitutionDto.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .multilingualDescription(CollectionMethodTestFixture.newMulti())
      .build();
  }
}
