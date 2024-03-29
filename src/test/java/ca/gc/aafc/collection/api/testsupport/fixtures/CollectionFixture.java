package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.CollectionIdentifier;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.net.URI;
import java.util.Collections;

import org.apache.commons.lang3.RandomStringUtils;

public final class CollectionFixture {

  private CollectionFixture() {
  }

  public static CollectionDto.CollectionDtoBuilder newCollection() {
    return CollectionDto.builder()
      .code(RandomStringUtils.randomAlphabetic(4))
      .name(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .group(RandomStringUtils.randomAlphabetic(4))
      .webpage("https://github.com/DINA-Web")
      .contact("514-123-4567 \n john.doe@canada.ca")
      .address("123 Street \n City")
      .remarks(RandomStringUtils.randomAlphabetic(30))
      .identifiers(Collections.singletonList(CollectionIdentifier.builder()
        .type(CollectionIdentifier.IdentifierType.INDEX_HERBARIORUM)
        .uri(URI.create("https://www.ORCID.org/ORCID/" +
          TestableEntityFactory.generateRandomName(5)))
        .build()));
  }
}
