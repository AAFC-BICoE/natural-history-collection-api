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
      .isolatedBy(UUID.randomUUID())
      .isolatedOn(LocalDate.now())
      .isolationRemarks("isolation remarks")
      .receivedFrom(UUID.randomUUID())
      .receivedDate(LocalDate.now())
      .receptionRemarks("reception remarks")
      .group("aafc")
      .build();

    acquisitionEventService.create(acquisitionEvent);

    AcquisitionEvent result = acquisitionEventService.findOne(acquisitionEvent.getUuid(), AcquisitionEvent.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());

    Assertions.assertEquals(acquisitionEvent.getIsolatedBy(), result.getIsolatedBy());
    Assertions.assertEquals(acquisitionEvent.getIsolatedOn(), result.getIsolatedOn());
    Assertions.assertEquals(acquisitionEvent.getIsolationRemarks(), result.getIsolationRemarks());
    
    Assertions.assertEquals(acquisitionEvent.getReceivedFrom(), result.getReceivedFrom());
    Assertions.assertEquals(acquisitionEvent.getReceivedDate(), result.getReceivedDate());
    Assertions.assertEquals(acquisitionEvent.getReceptionRemarks(), result.getReceptionRemarks());
  }
  
}
