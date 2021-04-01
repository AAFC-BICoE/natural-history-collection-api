package ca.gc.aafc.collection.api.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.CollectingEvent.GeoreferenceVerificationStatus;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  @Inject
  private MessageSource messageSource;

  public CollectingEventService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    validateGeoReferenceAssertions(entity);
    entity.setUuid(UUID.randomUUID());
    assignAutomaticValues(entity);
    linkAssertions(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    validateGeoReferenceAssertions(entity);
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

  private void validateGeoReferenceAssertions(CollectingEvent entity) {
    if (entity.getDwcGeoreferenceVerificationStatus() == GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE) {
      List<GeoreferenceAssertion> geos = entity.getGeoReferenceAssertions();
      if (CollectionUtils.isNotEmpty(geos)) {
        String errorMsg = messageSource.getMessage(
          "exception.collectingEvent.geoReferenceNotPossible.message",
          null,
          LocaleContextHolder.getLocale());
        throw new ValidationException(errorMsg);
      }
    }
  }

}
