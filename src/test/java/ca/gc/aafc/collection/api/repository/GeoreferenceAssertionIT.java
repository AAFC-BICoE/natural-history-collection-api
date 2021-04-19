package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;
import ca.gc.aafc.collection.api.service.GeoReferenceAssertionService;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "keycloak.enabled=true")
public class GeoreferenceAssertionIT extends CollectionModuleBaseIT {

  @Inject
  private GeoReferenceAssertionService georeferenceAssertionService;

  private final static double latitude = 12.34;
  private final static double longitude = 56.78;
  private static final LocalDate testGeoreferencedDate = LocalDate.now();

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void createUnlinkedGeoreference_success() {
    GeoreferenceAssertion ga = newGeoreferenceAssertion();
    georeferenceAssertionService.create(ga);
    assertEquals(latitude, ga.getDwcDecimalLatitude());
    assertEquals(longitude, ga.getDwcDecimalLongitude());
  }

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void georeferenceVerificationStatusEqualsGeoreferencingNotPossible_throwsIllegalArgumentException() {
    GeoreferenceAssertion ga = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .dwcDecimalLongitude(longitude)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .dwcGeoreferenceVerificationStatus(GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE)
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> georeferenceAssertionService.create(ga));

    String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }

  private GeoreferenceAssertion newGeoreferenceAssertion() {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .dwcDecimalLongitude(longitude)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .build();
  }

}
