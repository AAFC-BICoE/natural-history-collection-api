package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.UUID;

public class AssemblageTestFixture {

  public static final String GROUP = "aafc";

  public static AssemblageDto newAssemblage() {
    AssemblageDto assemblageDto = new AssemblageDto();
    assemblageDto.setGroup(GROUP);
    assemblageDto.setName(RandomStringUtils.randomAlphabetic(5));
    assemblageDto.setMultilingualDescription(CollectionMethodTestFixture.newMulti());
    assemblageDto.setAttachment(List.of(
            ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return assemblageDto;
  }
}
