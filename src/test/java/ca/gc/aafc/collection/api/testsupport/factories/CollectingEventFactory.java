package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class CollectingEventFactory implements TestableEntityFactory<CollectingEvent> {

  public static final CollectingEvent.GeographicPlaceNameSource GEOGRAPHIC_PLACE_NAME_SOURCE = CollectingEvent.GeographicPlaceNameSource.OSM;

  public static final GeographicPlaceNameSourceDetail.Country TEST_COUNTRY =
    GeographicPlaceNameSourceDetail.Country.builder()
      .code("CA").name("Canada").build();
  public static final GeographicPlaceNameSourceDetail.SourceAdministrativeLevel TEST_PROVINCE =
    GeographicPlaceNameSourceDetail.SourceAdministrativeLevel.builder().id("BDH32-4")
      .element("N").placeType("province").name("Test Province")
      .build();

  @Override
  public CollectingEvent getEntityInstance() {
    return newCollectingEvent().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static CollectingEvent.CollectingEventBuilder newCollectingEvent() {
    return CollectingEvent
      .builder()
      .group("test group")
      .createdBy("test user");
  }

  public static GeographicPlaceNameSourceDetail newGeographicPlaceNameSourceDetail() {
    return GeographicPlaceNameSourceDetail
      .builder()
      .country(TEST_COUNTRY)
      .stateProvince(TEST_PROVINCE)
      .sourceUrl("https://github.com/orgs/AAFC-BICoE/dashboard").build();
  }

}
