package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.entity.AgentRoles;

public class ExpeditionTestFixture {

  private static final String GROUP = "aafc";
  private static final LocalDate START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate END_DATE = LocalDate.now();

  public static ExpeditionDto newExpedition() {
    ExpeditionDto expeditionDto = new ExpeditionDto();
    expeditionDto.setName(RandomStringUtils.randomAlphabetic(5));
    expeditionDto.setGeographicContext(RandomStringUtils.randomAlphabetic(5));
    expeditionDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    expeditionDto.setStartDate(START_DATE);
    expeditionDto.setEndDate(END_DATE);
    expeditionDto.setGroup(GROUP);

    expeditionDto.setParticipants(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("person").build()));
    return expeditionDto;
  }

}
