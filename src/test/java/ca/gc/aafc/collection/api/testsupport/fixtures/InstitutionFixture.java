package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.entities.InstitutionIdentifier;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

public final class InstitutionFixture {

  private InstitutionFixture() {
  }

  public static InstitutionDto.InstitutionDtoBuilder newInstitution() {
    return InstitutionDto.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
      .webpage("https://github.com/DINA-Web")
      .address("123 Street \n City")
      .remarks(org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(30))
      .identifiers((Collections.singletonList(InstitutionIdentifier.builder()
        .type(InstitutionIdentifier.IdentifierType.GRSCICOLL)
        .uri(URI.create("https://www.ORCID.org/ORCID/" +
          TestableEntityFactory.generateRandomName(5)))
        .build())));
  }

  public static Institution.InstitutionBuilder<?, ?> newInstitutionEntity() {
    return Institution.builder()
      .uuid(UUID.randomUUID())
      .name(org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(3))
      .createdBy(org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(3))
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
      .webpage("https://github.com/DINA-Web")
      .address("123 Street \n City")
      .remarks(org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(30));
  }
}
