package ca.gc.aafc.collection.api.service;

import java.time.OffsetDateTime;
import java.util.List;
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
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    linkAssertions(entity);
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

}
