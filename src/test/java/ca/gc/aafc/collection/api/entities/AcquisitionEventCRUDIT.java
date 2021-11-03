package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.AcquisitionEventFactory;

public class AcquisitionEventCRUDIT extends CollectionModuleBaseIT {

  @Test
  void find() {
    AcquisitionEvent acquisitionEvent = AcquisitionEventFactory.newAcquisitionEvent()
      .externallyIsolatedBy(UUID.randomUUID())
      .externallyIsolatedOn(LocalDate.now())
      .externallyIsolationRemarks("externally isolation remarks")
      .receivedFrom(UUID.randomUUID())
      .receivedDate(LocalDate.now())
      .receptionRemarks("reception remarks")
      .build();

    acquisitionEventService.create(acquisitionEvent);

    AcquisitionEvent result = acquisitionEventService.findOne(acquisitionEvent.getUuid(), AcquisitionEvent.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());

    Assertions.assertEquals(acquisitionEvent.getExternallyIsolatedBy(), result.getExternallyIsolatedBy());
    Assertions.assertEquals(acquisitionEvent.getExternallyIsolatedOn(), result.getExternallyIsolatedOn());
    Assertions.assertEquals(acquisitionEvent.getExternallyIsolationRemarks(), result.getExternallyIsolationRemarks());
    
    Assertions.assertEquals(acquisitionEvent.getReceivedFrom(), result.getReceivedFrom());
    Assertions.assertEquals(acquisitionEvent.getReceivedDate(), result.getReceivedDate());
    Assertions.assertEquals(acquisitionEvent.getReceptionRemarks(), result.getReceptionRemarks());
  }
  
}
