package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class MaterialSampleService extends DefaultDinaService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public MaterialSampleService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull MaterialSampleValidator materialSampleValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService
  ) {
    super(baseDAO, sv);
    this.materialSampleValidator = materialSampleValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.postgresHierarchicalDataService = postgresHierarchicalDataService;
  }

  @Override
  public <T> List<T> findAll(
    @NonNull Class<T> entityClass,
    @NonNull PredicateSupplier<T> where,
    BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy,
    int startIndex,
    int maxResult
  ) {
    List<T> all = super.findAll(entityClass, where, orderBy, startIndex, maxResult);
    if (CollectionUtils.isNotEmpty(all) && entityClass == MaterialSample.class) {
      all.forEach(t -> {
        if (t instanceof MaterialSample) {
          setHierarchy((MaterialSample) t);
        }
      });
    }
    return all;
  }

  private void setHierarchy(MaterialSample sample) {
    sample.setHierarchy(postgresHierarchicalDataService.getHierarchy(
      sample.getId(),
      "material_sample",
      "id",
      "uuid",
      "parent_material_sample_id",
      "material_sample_name"
    ));
  }

  @Override
  protected void preCreate(MaterialSample entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(MaterialSample entity) {
    applyBusinessRule(entity, materialSampleValidator);
    validateManagedAttribute(entity);
  }

  private void validateManagedAttribute(MaterialSample entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
      CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.MATERIAL_SAMPLE);
  }

  @Override
  public MaterialSample create(MaterialSample entity) {
    MaterialSample sample = super.create(entity);
    return detachParent(sample);
  }

  @Override
  public MaterialSample update(MaterialSample entity) {
    MaterialSample sample = super.update(entity);
    return detachParent(sample);
  }

  private MaterialSample detachParent(MaterialSample sample) {
    if (sample.getParentMaterialSample() != null) {
      detach(sample.getParentMaterialSample());
    }
    return sample;
  }
}
