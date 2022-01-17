package ca.gc.aafc.collection.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.collection.api.validation.AssociationValidator;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;

import lombok.NonNull;

@Service
public class MaterialSampleService extends MessageProducingService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;
  private final AssociationValidator associationValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public MaterialSampleService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull MaterialSampleValidator materialSampleValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
    @NonNull AssociationValidator associationValidator,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService,
    ApplicationEventPublisher eventPublisher
  ) {
    super(baseDAO, sv, MaterialSampleDto.TYPENAME, eventPublisher);
    this.materialSampleValidator = materialSampleValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.associationValidator = associationValidator;
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
    checkSingularDeterminationIsPrimary(entity);
  }

  @Override
  protected void preUpdate(MaterialSample entity) {
    linkAssociations(entity);
    checkSingularDeterminationIsPrimary(entity);
  }

  /**
   * Check if there is only one determination and if isPrimary is null or false
   * set isPrimary to true. If the material sample is a Mixed Organism type then
   * it will not automatically be set since it's possible for a Mixed Organism not
   * to have a primary determination.
   * 
   * @param materialSample
   */
  private void checkSingularDeterminationIsPrimary(MaterialSample materialSample) {
    // Automatically set the primary determination if there is one determination. (Unless mixed sample type is Mixed Organism.)
    if (CollectionUtils.size(materialSample.getDetermination()) == 1 &&
        BooleanUtils.isFalse(materialSample.getDetermination().get(0).getIsPrimary()) &&
        !materialSample.isType(MaterialSampleType.MIXED_ORGANISMS_UUID)) {

      Determination determination = materialSample.getDetermination().get(0).toBuilder()
        .isPrimary(true)
        .build();

      materialSample.setDetermination(new ArrayList<>(List.of(determination)));
    }
  }

  private void linkAssociations(MaterialSample entity) {
    if (CollectionUtils.isNotEmpty(entity.getAssociations())) {
      entity.getAssociations().forEach(association -> {
        UUID associatedUuid = association.getAssociatedSample().getUuid();
        association.setSample(entity);
        association.setAssociatedSample(this.findOne(associatedUuid, MaterialSample.class));
      });
    }
  }

  @Override
  public void validateBusinessRules(MaterialSample entity) {
    applyBusinessRule(entity, materialSampleValidator);
    validateManagedAttribute(entity);
    validateDeterminationManagedAttribute(entity);
    validateAssociations(entity);
  }

  private void validateManagedAttribute(MaterialSample entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
      CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.MATERIAL_SAMPLE);
  }

  private void validateDeterminationManagedAttribute(MaterialSample entity) {
    if (CollectionUtils.isNotEmpty(entity.getDetermination())) {
      for (Determination determination : entity.getDetermination()) {
        if (determination.getManagedAttributes() != null) {
          collectionManagedAttributeValueValidator.validate(
            entity.getUuid().toString() + StringUtils.defaultString(determination.getScientificName()), 
            determination, 
            determination.getManagedAttributes(), 
            CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.DETERMINATION);
        }
      }
    }
  }

  private void validateAssociations(MaterialSample entity) {
    if (CollectionUtils.isNotEmpty(entity.getAssociations())) {
      int associationIndex = 0;
      for (Association association : entity.getAssociations()) {
        applyBusinessRule(entity.getUuid().toString() + associationIndex, association, associationValidator);
        associationIndex++;
      }
    }
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
   *
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
