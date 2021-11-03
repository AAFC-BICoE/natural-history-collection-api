package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.UUID;

import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.AcquisitionEventDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

public class AcquisitionEventTestFixture {

  public static final String CREATED_BY = RandomStringUtils.randomAlphabetic(4);

  public static final UUID EXTERNALLY_ISOLATED_BY = UUID.randomUUID();
  public static final LocalDate EXTERNALLY_ISOLATED_ON = LocalDate.now();
  public static final String EXTERNALLY_ISOLATION_REMARKS = RandomStringUtils.randomAlphabetic(10);

  public static final UUID RECEIVED_FROM = UUID.randomUUID();
  public static final LocalDate RECEIVED_DATE = LocalDate.now();
  public static final String RECEPTION_REMARKS = RandomStringUtils.randomAlphabetic(10);

  public static AcquisitionEventDto newAcquisitionEvent() {
    AcquisitionEventDto acquisitionEventDto = new AcquisitionEventDto();
    acquisitionEventDto.setCreatedBy(CREATED_BY);
    acquisitionEventDto.setExternallyIsolatedBy(ExternalRelationDto.builder().id(EXTERNALLY_ISOLATED_BY.toString()).type("person").build());
    acquisitionEventDto.setExternallyIsolatedOn(EXTERNALLY_ISOLATED_ON);
    acquisitionEventDto.setExternallyIsolationRemarks(EXTERNALLY_ISOLATION_REMARKS);

    acquisitionEventDto.setReceivedFrom(ExternalRelationDto.builder().id(RECEIVED_FROM.toString()).type("person").build());
    acquisitionEventDto.setReceivedDate(RECEIVED_DATE);
    acquisitionEventDto.setReceptionRemarks(RECEPTION_REMARKS);
    
    return acquisitionEventDto;
  }
  
}
