package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.net.URI;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.ExpeditionIdentifier;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.entity.AgentRoles;

public class ExpeditionFixture {

  private static final String GROUP = "aafc";
  private static final LocalDate START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate END_DATE = LocalDate.now();

  public static ExpeditionDto.ExpeditionDtoBuilder newExpedition() {
    List<ExpeditionIdentifier> identifiers = new ArrayList<>();
    identifiers.add(ExpeditionIdentifier.builder()
      .type(ExpeditionIdentifier.IdentifierType.WIKIDATA)
      .uri(URI.create("https://www.wikidata.org/wiki/Q4558719"))
      .build());

    return ExpeditionDto.builder()
    .name(RandomStringUtils.randomAlphabetic(5))
    .geographicContext(RandomStringUtils.randomAlphabetic(5))
    .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
    .startDate(START_DATE)
    .endDate(END_DATE)
    .group(GROUP)
    .identifiers(identifiers);
  }

}
