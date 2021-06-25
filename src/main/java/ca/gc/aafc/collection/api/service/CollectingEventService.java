package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.GeoreferenceAssertionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  private final CollectingEventValidator collectingEventValidator;
  private final BaseDAO baseDAO;
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
    this.baseDAO = baseDAO;
    this.georeferenceAssertionValidator = georeferenceAssertionValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    cleanupManagedAttributes(entity);
    assignAutomaticValues(entity);
    linkAssertions(entity, entity.getGeoReferenceAssertions());
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    cleanupManagedAttributes(entity);
    assignAutomaticValues(entity);
    resolveIncomingAssertion(entity);
  }

  @Override
  public void validateBusinessRules(CollectingEvent entity) {
    applyBusinessRule(entity, collectingEventValidator);
    validateAssertions(entity);

    List<GeoreferenceAssertion> incomingAssertions = entity.getGeoReferenceAssertions();
    entity.setGeoReferenceAssertions(null); // Set null due to flushing mechanics with validateManagedAttribute

    validateManagedAttribute(entity);

    if (CollectionUtils.isNotEmpty(incomingAssertions)) {
      entity.setGeoReferenceAssertions(incomingAssertions);
    }
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

  private void validateAssertions(@NonNull CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> validateGeoreferenceAssertion(
        geo,
        entity.getUuid().toString()));
    }
  }

  private void validateManagedAttribute(CollectingEvent entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes());
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

}
