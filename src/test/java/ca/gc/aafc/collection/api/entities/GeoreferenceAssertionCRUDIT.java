package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

public class GeoreferenceAssertionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private static final LocalDate testGeoreferencedDate = LocalDate.now();
  private static final GeoreferenceVerificationStatus georeferenceVerificationStatus = GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE;


  private List<UUID> agentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

  @Test
  public void testSave() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .georeferencedBy(agentIdentifiers)  
      .build();
    assertNull(geoReferenceAssertion.getId());
    dbService.save(geoReferenceAssertion);
    assertNotNull(geoReferenceAssertion.getId());
    assertEquals(agentIdentifiers, geoReferenceAssertion.getGeoreferencedBy());
  }

  @Test
  public void testFind() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
        .dwcDecimalLatitude(12.123456)
        .dwcDecimalLongitude(45.01)
        .dwcCoordinateUncertaintyInMeters(10)
        .dwcGeoreferencedDate(testGeoreferencedDate)
        .build();
    dbService.save(geoReferenceAssertion);

    GeoreferenceAssertion fetchedGeoreferenceAssertion = dbService.find(GeoreferenceAssertion.class, geoReferenceAssertion.getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedGeoreferenceAssertion.getId());
    assertEquals(12.123456, fetchedGeoreferenceAssertion.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedGeoreferenceAssertion.getDwcDecimalLongitude());
    assertEquals(10, fetchedGeoreferenceAssertion.getDwcCoordinateUncertaintyInMeters());
    assertEquals(testGeoreferencedDate, fetchedGeoreferenceAssertion.getDwcGeoreferencedDate());
    assertNotNull(fetchedGeoreferenceAssertion.getCreatedOn());
  }

  @Test
  public void Validate_GeoreferenceVerificationStatus_ThrowsException() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
        .dwcDecimalLatitude(12.123456)
        .dwcDecimalLongitude(45.01)
        .dwcCoordinateUncertaintyInMeters(10)
        .dwcGeoreferencedDate(testGeoreferencedDate)
        .dwcGeoreferenceVerificationStatus(georeferenceVerificationStatus)
        .build();
    ConstraintViolationException exception =  assertThrows(ConstraintViolationException.class, () -> {
      dbService.save(geoReferenceAssertion);
    });

    String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void Validate_DefaultValidation_ThrowsException() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(-300.0)
      .dwcDecimalLongitude(45.01)
      .dwcCoordinateUncertaintyInMeters(10)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .build();
    ConstraintViolationException exception =  assertThrows(ConstraintViolationException.class, () -> {
        dbService.save(geoReferenceAssertion);
    });

    String expectedMessage = "must be greater than or equal to";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }
}
