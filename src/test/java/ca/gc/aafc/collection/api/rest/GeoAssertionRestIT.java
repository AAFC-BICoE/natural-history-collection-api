package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class GeoAssertionRestIT extends BaseRestAssuredTest {

  protected GeoAssertionRestIT() {
    super("/api/v1/collecting-event");
  }

  @Test
  void update_whenGeoAssertionsUpdated_GeosUpdated() {
    GeoreferenceAssertionDto geo1 = newAssertion(2);
    GeoreferenceAssertionDto geo2 = newAssertion(3);
    GeoreferenceAssertionDto geo3 = newAssertion(4);

    CollectingEventDto dto = newEventDto();
    dto.setGeoReferenceAssertions(List.of(geo1));
    String id = JsonAPITestHelper.extractId(super.sendPost(mapObj(dto)));


    dto.setGeoReferenceAssertions(List.of(geo2,geo3));

    super.sendPatch(id ,mapObj(dto));

    super.sendGet(id).log().all(true);
  }

  private static GeoreferenceAssertionDto newAssertion(double latitude) {
    return GeoreferenceAssertionDto.builder()
      .dwcDecimalLongitude(2.0)
      .dwcDecimalLatitude(latitude)
      .createdBy("dfa")
      .build();
  }

  private CollectingEventDto newEventDto() {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("aafc");
    ce.setStartEventDateTime(ISODateTime.parse(LocalDateTime.now().toString()).toString());
    ce.setCollectors(null);
    ce.setAttachment(null);
    ce.setCreatedBy("dina");
    return ce;
  }

  private static Map<String, Object> mapObj(CollectingEventDto dto) {
    return JsonAPITestHelper.toJsonAPIMap(
      "collecting-event", JsonAPITestHelper.toAttributeMap(dto), null, null);
  }
}
