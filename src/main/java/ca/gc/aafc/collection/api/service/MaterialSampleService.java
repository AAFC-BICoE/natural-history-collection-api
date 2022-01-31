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
    setupDeterminations(entity);
  }

  @Override
  protected void preUpdate(MaterialSample entity) {
    linkAssociations(entity);
    setupDeterminations(entity);
  }

  /**
   * Will automatically set a UUID for each organism if one has not already been given.
   * 
   * For each organism a material sample has, if it contains a single determination
   * it will automatically set it to the primary determination.
   * 
   * This step should be performed before the validation.
   * 
   * @param materialSample material sample to check the organism determinations.
   */
  private void setupDeterminations(MaterialSample materialSample) {
    // Automatically set the primary determination if there is one determination. 
    if (CollectionUtils.isNotEmpty(materialSample.getOrganism())) {

      // This applies for each organism a material sample has.
      materialSample.getOrganism().forEach(organism -> {

        // If no UUID has been set yet, generate a random one.
        if (organism.getUuid() == null) {
          organism.setUuid(UUID.randomUUID());
        }

        // Check to see if one determination is present and is currently not primary.
        if (CollectionUtils.size(organism.getDetermination()) == 1 &&
            BooleanUtils.isFalse(organism.getDetermination().get(0).getIsPrimary())) {
          organism.getDetermination().get(0).setIsPrimary(true);
        }
      });
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
    // A material sample can have multiple organisms, each with it's own set of determinations.
    if (CollectionUtils.isNotEmpty(entity.getOrganism())) {
      entity.getOrganism().forEach(organism -> {

        // An organism can have multiple determinations, go through each of them and validate.
        if (CollectionUtils.isNotEmpty(organism.getDetermination())) {
          organism.getDetermination().forEach(determination -> {
            if (determination.getManagedAttributes() != null) {
              collectionManagedAttributeValueValidator.validate(
                organism.getUuid().toString() + StringUtils.defaultString(determination.getScientificName()), 
                determination, 
                determination.getManagedAttributes(), 
                CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.DETERMINATION);
            }            
          });
        }

      });
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
