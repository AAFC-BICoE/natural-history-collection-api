package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.validation.CollectingEventValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  private final CollectingEventValidator collectingEventValidator;
  private final GeoReferenceAssertionService geoReferenceAssertionService;
  private final BaseDAO baseDAO;

  public CollectingEventService(
    @NonNull BaseDAO baseDAO,
    @NonNull CollectingEventValidator collectingEventValidator,
    @NonNull GeoReferenceAssertionService geoReferenceAssertionService
  ) {
    super(baseDAO);
    this.collectingEventValidator = collectingEventValidator;
    this.geoReferenceAssertionService = geoReferenceAssertionService;
    this.baseDAO = baseDAO;
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    linkAssertions(entity, entity.getGeoReferenceAssertions());
    validateCollectingEvent(entity);
  }

  @Override
  public void preUpdate(CollectingEvent entity) {
    cleanupManagedAttributeValues(entity);
    assignAutomaticValues(entity);
    resolveIncomingAssertion(entity);
    validateCollectingEvent(entity);
  }

  private void resolveIncomingAssertion(CollectingEvent entity) {
    List<GeoreferenceAssertion> incoming = entity.getGeoReferenceAssertions();
    entity.setGeoReferenceAssertions(null);
    fetchAssertions(entity).forEach(geoReferenceAssertionService::delete);

    // flush to Update hibernate session
    baseDAO.createWithEntityManager(entityManager -> {
      entityManager.flush();
      return null;
    });

    if (CollectionUtils.isNotEmpty(incoming)) {
      linkAssertions(entity, incoming);
      entity.setGeoReferenceAssertions(new ArrayList<>(incoming));
    }
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
