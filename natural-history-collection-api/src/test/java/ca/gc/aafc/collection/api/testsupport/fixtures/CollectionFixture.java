package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.RandomStringUtils;

public final class CollectionFixture {

  private CollectionFixture() {
  }

  @SneakyThrows(MalformedURLException.class)
  public static CollectionDto.CollectionDtoBuilder newCollection() {
    return CollectionDto.builder()
      .code(RandomStringUtils.randomAlphabetic(4))
      .name(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .group(RandomStringUtils.randomAlphabetic(4))
      .webpage(new URL("https://github.com/DINA-Web"))
      .contact("514-123-4567 \n john.doe@canada.ca")
      .address("123 Street \n City")
      .remarks(RandomStringUtils.randomAlphabetic(30));
  }
}
