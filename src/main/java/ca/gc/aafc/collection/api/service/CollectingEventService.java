package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.CollectingEvent.ManagedAttributeValue;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
import ca.gc.aafc.collection.api.validation.GeoreferenceAssertionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidator;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import javax.inject.Named;
import javax.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  private final CollectingEventValidator collectingEventValidator;
  private final BaseDAO baseDAO;
  private final GeoreferenceAssertionValidator georeferenceAssertionValidator;
  private final ManagedAttributeValueValidator<CollectionManagedAttribute> managedAttributeValueValidator;

  public CollectingEventService(
    @NonNull BaseDAO baseDAO,
    @NonNull CollectingEventValidator collectingEventValidator,
    @NonNull GeoreferenceAssertionValidator georeferenceAssertionValidator,
    @Named("validationMessageSource") MessageSource messageSource,
    CollectionManagedAttributeService collectionManagedAttributeService
    
  ) {
    super(baseDAO);
    this.collectingEventValidator = collectingEventValidator;
    this.baseDAO = baseDAO;
    this.georeferenceAssertionValidator = georeferenceAssertionValidator;
    this.managedAttributeValueValidator = new ManagedAttributeValueValidator<CollectionManagedAttribute>(messageSource, collectionManagedAttributeService);
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    linkAssertions(entity, entity.getGeoReferenceAssertions());
    validateCollectingEvent(entity);
    validateAssertions(entity);
    validateManagedAttribute(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    resolveIncomingAssertion(entity);
    validateCollectingEvent(entity);
    validateAssertions(entity);
    validateManagedAttribute(entity);
  }

  private void resolveIncomingAssertion(CollectingEvent entity) {
    List<GeoreferenceAssertion> incomingAssertions = entity.getGeoReferenceAssertions();
    entity.setGeoReferenceAssertions(null); // Set null due to flushing mechanics with fetch
    List<GeoreferenceAssertion> currentAssertions = fetchAssertions(entity);

    if (CollectionUtils.isEmpty(incomingAssertions)) {
      currentAssertions.forEach(baseDAO::delete);
      return; // Clear assertions and exit
    }

    int currentSize = currentAssertions.size();
    for (int i = 0; i < incomingAssertions.size(); i++) { // Merge over existing assertions
      GeoreferenceAssertion in = incomingAssertions.get(i);
      in.setCollectingEvent(entity);
      in.setIndex(i);

      if (i < currentSize) {
        GeoreferenceAssertion current = currentAssertions.get(i);
        in.setId(current.getId()); // Set id so hibernate will merge over current
        baseDAO.update(in);
      }
    }

    while (currentSize > incomingAssertions.size()) { // Remove remaining current assertions
      baseDAO.delete(currentAssertions.get(currentSize - 1));
      currentSize--;
    }

    entity.setGeoReferenceAssertions(new ArrayList<>(incomingAssertions));
  }

  private List<GeoreferenceAssertion> fetchAssertions(CollectingEvent entity) {
    return this.findAll(
      GeoreferenceAssertion.class,
      (cb, root) -> new Predicate[]{cb.equal(root.get("collectingEvent"), entity)},
      null, 0, Integer.MAX_VALUE)
      .stream()
      .sorted(Comparator.comparing(GeoreferenceAssertion::getIndex))
      .collect(Collectors.toList());
  }

  private static void linkAssertions(
    CollectingEvent entity,
    List<GeoreferenceAssertion> geoReferenceAssertions
  ) {
    if (CollectionUtils.isNotEmpty(geoReferenceAssertions)) {
      for (int i = 0; i < geoReferenceAssertions.size(); i++) {
        GeoreferenceAssertion geoReferenceAssertion = geoReferenceAssertions.get(i);
        geoReferenceAssertion.setIndex(i);
        geoReferenceAssertion.setCollectingEvent(entity);
      }
    }
  }

  private static void assignAutomaticValues(CollectingEvent entity) {
    if (entity.getGeographicPlaceNameSourceDetail() != null) {
      entity.getGeographicPlaceNameSourceDetail().setRecordedOn(OffsetDateTime.now());
    }
  }

  public void validateCollectingEvent(CollectingEvent entity) {
    validateBusinessRules(entity, collectingEventValidator);
  }

  private void validateAssertions(@NonNull CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> validateGeoreferenceAssertion(
        geo,
        entity.getUuid().toString()));
    }
  }

  private void validateManagedAttribute(CollectingEvent entity) {
    Map<String, String> newMap = new HashMap<String, String>();
    for (Map.Entry<String, ManagedAttributeValue> entry : entity.getManagedAttributeValues().entrySet()) {
        newMap.put(entry.getKey(), entry.getValue().getAssignedValue());
      }
    managedAttributeValueValidator.validate(entity, newMap);
  }

  public void validateGeoreferenceAssertion(@NonNull GeoreferenceAssertion geo, @NonNull String eventUUID) {
    Errors errors = new BeanPropertyBindingResult(geo, eventUUID + "/" + geo.getIndex());
    georeferenceAssertionValidator.validate(geo, errors);

    if (!errors.hasErrors()) {
      return;
    }

    Optional<String> errorMsg = errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).findAny();
    errorMsg.ifPresent(msg -> {
      throw new IllegalArgumentException(msg);
    });
  }

  private void cleanupManagedAttributeValues(CollectingEvent entity) {
    var values = entity.getManagedAttributeValues();
    if (values == null) {
      return;
    }
    // Remove blank Managed Attribute values:
    values.entrySet().removeIf(
      entry -> StringUtils.isBlank(entry.getValue().getAssignedValue())
    );
  }

}
