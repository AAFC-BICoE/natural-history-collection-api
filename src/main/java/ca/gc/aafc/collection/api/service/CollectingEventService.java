package ca.gc.aafc.collection.api.service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  public CollectingEventService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    assignAutomaticValues(entity);
    linkAssertions(entity);
    handlePrimaryGeoreferenceAssertion(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    linkAssertions(entity);
    handlePrimaryGeoreferenceAssertion(entity);
  }

  private static void linkAssertions(CollectingEvent entity) {
    List<GeoreferenceAssertion> geos = entity.getGeoReferenceAssertions();
    if (CollectionUtils.isNotEmpty(geos)) {
      geos.forEach(geoReferenceAssertion -> geoReferenceAssertion.setCollectingEvent(entity));
    }
    assignAutomaticValues(entity);
  }

  private static void assignAutomaticValues(CollectingEvent entity) {
    if (entity.getGeographicPlaceNameSourceDetail() != null) {
      entity.getGeographicPlaceNameSourceDetail().setRecordedOn(OffsetDateTime.now());
    }
  }

  /**
   * the primary assertion's FK can only be removed if there is only 1 assertion (but it can be changed to another assertion)
   * if the primary assertion's FK is removed (since there is only 1 left) the regular FK of the last assertion is also removed
   * 
   * @param entity
   */
  private static void handlePrimaryGeoreferenceAssertion(CollectingEvent entity) {
    int assertionsSize = Optional.ofNullable(entity.getGeoReferenceAssertions())
        .map(List::size).orElse(0);

    //Set primary assertion to first assertion in list by default if assertion list is bigger than 1
    if (assertionsSize > 1 && entity.getPrimaryGeoreferenceAssertion() == null) {
      entity.setPrimaryGeoreferenceAssertion(entity.getGeoReferenceAssertions().get(0));
      return;
    }

    if ((assertionsSize == 1 && entity.getPrimaryGeoreferenceAssertion() == null) ||
        (assertionsSize == 0 && entity.getPrimaryGeoreferenceAssertion() != null)) {
      entity.setGeoReferenceAssertions(Collections.emptyList());
      entity.setPrimaryGeoreferenceAssertion(null);
    }
  }
}
