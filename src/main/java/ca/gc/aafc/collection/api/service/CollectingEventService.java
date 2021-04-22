package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
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

  public CollectingEventService(
    @NonNull BaseDAO baseDAO,
    @NonNull CollectingEventValidator collectingEventValidator,
    @NonNull GeoreferenceAssertionValidator georeferenceAssertionValidator
  ) {
    super(baseDAO);
    this.collectingEventValidator = collectingEventValidator;
    this.baseDAO = baseDAO;
    this.georeferenceAssertionValidator = georeferenceAssertionValidator;
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    linkAssertions(entity, entity.getGeoReferenceAssertions());
    validateCollectingEvent(entity);
    validateAssertions(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    resolveIncomingAssertion(entity);
    validateCollectingEvent(entity);
    validateAssertions(entity);
  }

  private void resolveIncomingAssertion(CollectingEvent entity) {
    List<GeoreferenceAssertion> incomingAssertions = entity.getGeoReferenceAssertions();
    entity.setGeoReferenceAssertions(null);
    List<GeoreferenceAssertion> currentAssertions = fetchAssertions(entity);

    int currentSize = currentAssertions.size();
    for (int i = 0; i < incomingAssertions.size(); i++) {
      GeoreferenceAssertion in = incomingAssertions.get(i);
      in.setCollectingEvent(entity);
      in.setIndex(i);

      if (i < currentSize) {
        GeoreferenceAssertion current = currentAssertions.get(i);
        in.setId(current.getId());
        baseDAO.createWithEntityManager(entityManager -> entityManager.merge(in));
      }
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
    Errors errors = new BeanPropertyBindingResult(entity, entity.getUuid().toString());
    collectingEventValidator.validate(entity, errors);

    if (!errors.hasErrors()) {
      return;
    }

    Optional<String> errorMsg = errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).findAny();
    errorMsg.ifPresent(msg -> {
      throw new IllegalArgumentException(msg);
    });
  }

  private void validateAssertions(@NonNull CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getGeoReferenceAssertions())) {
      entity.getGeoReferenceAssertions().forEach(geo -> validateGeoreferenceAssertion(
        geo,
        entity.getUuid().toString()));
    }
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
