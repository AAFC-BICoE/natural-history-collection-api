package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import ca.gc.aafc.dina.service.MessageProducingService;
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
public class MaterialSampleService extends MessageProducingService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public MaterialSampleService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull MaterialSampleValidator materialSampleValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService,
      MessageProducer messageProducer
  ) {
    super(baseDAO, sv, MaterialSampleDto.TYPENAME, messageProducer);
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
        MaterialSample.TABLE_NAME,
        MaterialSample.ID_COLUMN_NAME,
        MaterialSample.UUID_COLUMN_NAME,
        MaterialSample.PARENT_ID_COLUMN_NAME,
        MaterialSample.NAME_COLUMN_NAME
    ));
  }

  @Override
  protected void preCreate(MaterialSample entity) {
    entity.setUuid(UUID.randomUUID());
    linkAssociations(entity);
  }

  private void linkAssociations(MaterialSample entity) {
    if (CollectionUtils.isNotEmpty(entity.getAssociations())) {
      entity.getAssociations().forEach(association -> {
        association.setSample(entity);
        association.setAssociatedSample(
          this.findOne(association.getAssociatedSample().getUuid(), MaterialSample.class));
      });
    }
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

  /**
   * Detaches the parent to make sure it reloads its children list
   * @param sample
   * @return
   */
  private MaterialSample detachParent(MaterialSample sample) {
    if (sample.getParentMaterialSample() != null) {
      detach(sample.getParentMaterialSample());
    }
    return sample;
  }
}
