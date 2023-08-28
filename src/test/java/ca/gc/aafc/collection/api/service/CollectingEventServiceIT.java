package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectingEventServiceIT extends CollectionModuleBaseIT {

  @Test
  public void geographicData_onNoneProvided_valuesFromGeographicPlaceNameSourceDetail() {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = CollectingEventFactory.newGeographicPlaceNameSourceDetail();

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .dwcCountry(null)
      .dwcCountryCode(null)
      .dwcStateProvince(null)
      .geographicPlaceNameSource(CollectingEventFactory.GEOGRAPHIC_PLACE_NAME_SOURCE)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .build();
    collectingEventService.createAndFlush(collectingEvent);

    CollectingEvent collectingEventReloaded = collectingEventService.findOne(collectingEvent.getUuid(), CollectingEvent.class);

    assertEquals(CollectingEventFactory.TEST_COUNTRY.getCode(), collectingEventReloaded.getDwcCountryCode());
    assertEquals(CollectingEventFactory.TEST_COUNTRY.getName(), collectingEventReloaded.getDwcCountry());
    assertEquals(CollectingEventFactory.TEST_PROVINCE.getName(), collectingEventReloaded.getDwcStateProvince());
  }

  @Test
  public void geographicData_ifProvided_overwrittenByPlaceNameSourceDetail() {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = CollectingEventFactory.newGeographicPlaceNameSourceDetail();

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .dwcCountry("Arctic")
      .dwcCountryCode("AA")
      .dwcStateProvince("province")
      .geographicPlaceNameSource(CollectingEventFactory.GEOGRAPHIC_PLACE_NAME_SOURCE)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .build();
    collectingEventService.createAndFlush(collectingEvent);

    CollectingEvent collectingEventReloaded = collectingEventService.findOne(collectingEvent.getUuid(), CollectingEvent.class);

    assertEquals(CollectingEventFactory.TEST_COUNTRY.getCode(), collectingEventReloaded.getDwcCountryCode());
    assertEquals(CollectingEventFactory.TEST_COUNTRY.getName(), collectingEventReloaded.getDwcCountry());
    assertEquals(CollectingEventFactory.TEST_PROVINCE.getName(), collectingEventReloaded.getDwcStateProvince());
  }
}
