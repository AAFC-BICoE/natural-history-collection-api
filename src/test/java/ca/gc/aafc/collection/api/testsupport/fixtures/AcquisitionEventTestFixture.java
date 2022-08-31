package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.UUID;

import ca.gc.aafc.collection.api.dto.AcquisitionEventDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import org.apache.commons.lang3.RandomStringUtils;

public class AcquisitionEventTestFixture {

  public static final String CREATED_BY = RandomStringUtils.randomAlphabetic(4);

  public static final UUID ISOLATED_BY = UUID.randomUUID();
  public static final LocalDate ISOLATED_ON = LocalDate.now();
  public static final String ISOLATION_REMARKS = RandomStringUtils.randomAlphabetic(10);

  public static final UUID RECEIVED_FROM = UUID.randomUUID();
  public static final LocalDate RECEIVED_DATE = LocalDate.now();
  public static final String RECEPTION_REMARKS = RandomStringUtils.randomAlphabetic(10);

  public static final String GROUP = "aafc";

  public static AcquisitionEventDto newAcquisitionEvent() {
    AcquisitionEventDto acquisitionEventDto = new AcquisitionEventDto();
    acquisitionEventDto.setCreatedBy(CREATED_BY);
    acquisitionEventDto.setIsolatedBy(ExternalRelationDto.builder().id(ISOLATED_BY.toString()).type("person").build());
    acquisitionEventDto.setIsolatedOn(ISOLATED_ON);
    acquisitionEventDto.setIsolationRemarks(ISOLATION_REMARKS);

    acquisitionEventDto.setReceivedFrom(ExternalRelationDto.builder().id(RECEIVED_FROM.toString()).type("person").build());
    acquisitionEventDto.setReceivedDate(RECEIVED_DATE);
    acquisitionEventDto.setReceptionRemarks(RECEPTION_REMARKS);

    acquisitionEventDto.setGroup(GROUP);
    
    return acquisitionEventDto;
  }
  
}
