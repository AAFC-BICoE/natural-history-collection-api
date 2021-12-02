package ca.gc.aafc.collection.api.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.GeoreferenceAssertionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  private final CollectingEventValidator collectingEventValidator;
  private final GeoreferenceAssertionValidator georeferenceAssertionValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;

  public CollectingEventService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull CollectingEventValidator collectingEventValidator,
    @NonNull GeoreferenceAssertionValidator georeferenceAssertionValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator
  ) {
    super(baseDAO, sv);
    this.collectingEventValidator = collectingEventValidator;
    this.georeferenceAssertionValidator = georeferenceAssertionValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    cleanupManagedAttributes(entity);
    assignAutomaticValues(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    cleanupManagedAttributes(entity);
    assignAutomaticValues(entity);
  }

  @Override
  public void validateBusinessRules(CollectingEvent entity) {
    applyBusinessRule(entity, collectingEventValidator);
    validateAssertions(entity);
    validateManagedAttribute(entity);
  }

  private static void assignAutomaticValues(CollectingEvent entity) {
    if (entity.getGeographicPlaceNameSourceDetail() != null) {
      entity.getGeographicPlaceNameSourceDetail().setRecordedOn(OffsetDateTime.now());
    }
    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> geo.setCreatedOn(OffsetDateTime.now()));
    }
    entity.getPrimaryAssertion().ifPresentOrElse(//Set event Geom from primary assertion
      geo -> entity.setEventGeom(mapAssertionToGeometry(geo)),
      () -> entity.setEventGeom(null));
  }

  private void validateAssertions(@NonNull CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> applyBusinessRule(
        entity.getUuid().toString(), 
        geo, 
        georeferenceAssertionValidator
      ));
    }
  }

  private void validateManagedAttribute(CollectingEvent entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
        CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.COLLECTING_EVENT);
  }

  private void cleanupManagedAttributes(CollectingEvent entity) {
    var values = entity.getManagedAttributes();
    if (values == null) {
      return;
    }
    // Remove blank Managed Attribute values:
    values.entrySet().removeIf(
      entry -> StringUtils.isBlank(entry.getValue())
    );
  }

  private static Geometry mapAssertionToGeometry(GeoreferenceAssertionDto geo) {
    if (geo == null || geo.getDwcDecimalLongitude() == null || geo.getDwcDecimalLatitude() == null) {
      return null;
    }
    return JTS.to(DSL.point(CoordinateReferenceSystems.WGS84, DSL.g(
      geo.getDwcDecimalLongitude(), geo.getDwcDecimalLatitude())));
  }
}
