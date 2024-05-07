package ca.gc.aafc.collection.api.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.validation.CollectingEventExtensionValueValidator;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.GeoreferenceAssertionValidator;
import ca.gc.aafc.dina.extension.FieldExtensionValue;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.message.DocumentOperationType;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import lombok.NonNull;

@Service
public class CollectingEventService extends MessageProducingService<CollectingEvent> {

  private final CollectingEventValidator collectingEventValidator;
  private final GeoreferenceAssertionValidator georeferenceAssertionValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext validationContext;
  private final CollectingEventExtensionValueValidator extensionValueValidator;

  public CollectingEventService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull CollectingEventValidator collectingEventValidator,
    @NonNull GeoreferenceAssertionValidator georeferenceAssertionValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
    @NonNull CollectingEventExtensionValueValidator extensionValueValidator,
    ApplicationEventPublisher eventPublisher
  ) {
    super(baseDAO, sv, CollectingEventDto.TYPENAME,
      EnumSet.of(DocumentOperationType.UPDATE, DocumentOperationType.DELETE), eventPublisher);
    this.collectingEventValidator = collectingEventValidator;
    this.georeferenceAssertionValidator = georeferenceAssertionValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.validationContext = CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
            .from(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    this.extensionValueValidator = extensionValueValidator;
  }

  @Override
  protected void preCreate(CollectingEvent entity) {

    // allow user provided UUID
    if(entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }

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
    validateExtensionValues(entity);
    validateManagedAttribute(entity);
  }

  private static void assignAutomaticValues(CollectingEvent entity) {
    if (entity.getGeographicPlaceNameSourceDetail() != null) {
      entity.getGeographicPlaceNameSourceDetail().setRecordedOn(OffsetDateTime.now());
    }

    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> geo.setCreatedOn(OffsetDateTime.now()));
    }

    mapGeographicPlaceNameFromSource(entity);

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

  private void validateExtensionValues(@NonNull CollectingEvent entity) {
    if (MapUtils.isNotEmpty(entity.getExtensionValues())) {

      for (String currExt : entity.getExtensionValues().keySet()) {
        entity.getExtensionValues().get(currExt).forEach((k, v) -> applyBusinessRule(
          entity.getUuid().toString(),
          FieldExtensionValue.builder().extKey(currExt).extFieldKey(k).value(v).build(),
          extensionValueValidator
        ));
      }
    }
  }

  private void validateManagedAttribute(CollectingEvent entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(), validationContext);
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

  /**
   * Map GeographicPlaceNameSourceDetail to dwcCountryCode, dwcCountry and dwcStateProvince if required.
   * If a GeographicPlaceNameSource is specified, we must take the data from there. Otherwise, the data should be provided
   * directly in dwcCountryCode, dwcCountry and dwcStateProvince without any GeographicPlaceNameSource.
   * @param entity
   */
  private static void mapGeographicPlaceNameFromSource(CollectingEvent entity) {

    if (entity.getGeographicPlaceNameSource() == null) {
      return;
    }

    var country = entity.getGeographicPlaceNameSourceDetail().getCountry();
    if( country != null) {
      entity.setDwcCountryCode(country.getCode());
      entity.setDwcCountry(country.getName());
    }

    var stateProvince = entity.getGeographicPlaceNameSourceDetail().getStateProvince();
    if(stateProvince != null) {
      entity.setDwcStateProvince(stateProvince.getName());
    }
  }

  private static Point<G2D> mapAssertionToGeometry(GeoreferenceAssertionDto geo) {
    if (geo == null || geo.getDwcDecimalLongitude() == null || geo.getDwcDecimalLatitude() == null) {
      return null;
    }
    return DSL.point(CoordinateReferenceSystems.WGS84, DSL.g(
      geo.getDwcDecimalLongitude(), geo.getDwcDecimalLatitude()));
  }
}
