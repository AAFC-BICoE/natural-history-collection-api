package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.validation.StorageUnitCoordinatesValidator;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;
import ca.gc.aafc.dina.util.UUIDHelper;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Service
public class StorageUnitCoordinatesService extends DefaultDinaService<StorageUnitCoordinates> {

  private final StorageUnitCoordinatesValidator coordinatesValidator;

  public StorageUnitCoordinatesService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, StorageUnitCoordinatesValidator coordinatesValidator) {
    super(baseDAO, sv);
    this.coordinatesValidator = coordinatesValidator;
  }

  @Override
  protected void preCreate(StorageUnitCoordinates entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  @Override
  public <T> List<T> findAll(@NonNull Class<T> entityClass, @NonNull PredicateSupplier<T> where,
                             BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy,
                             int startIndex, int maxResult, @NonNull Set<String> includes,
                             @NonNull Set<String> relationships)  {

    List<T> itemsList = super.findAll(entityClass, where, orderBy, startIndex, maxResult, includes, relationships);
    if (StorageUnitCoordinates.class == entityClass) {
      itemsList.forEach(t -> setCellNumber((StorageUnitCoordinates) t));
    }
    return itemsList;
  }

  @Override
  public void validateBusinessRules(StorageUnitCoordinates entity) {
    applyBusinessRule(entity, coordinatesValidator);
  }

  private void setCellNumber(StorageUnitCoordinates entity) {
    StorageUnitType sut = entity.getEffectiveStorageUnitType();

    StorageGridLayout restriction = sut.getGridLayoutDefinition();
    entity.setCellNumber(restriction.calculateCellNumber(NumberLetterTranslator.toNumber(entity.getWellRow()), entity.getWellColumn()));
  }

  protected final void finalize() {
    // no-op
  }
}
