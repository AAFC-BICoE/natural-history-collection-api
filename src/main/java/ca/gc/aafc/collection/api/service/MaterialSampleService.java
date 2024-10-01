package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.dao.CollectionHierarchicalDataDAO;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.ImmutableMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.validation.AssociationValidator;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.IdentifierTypeValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleExtensionValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.collection.api.validation.RestrictionExtensionValueValidator;
import ca.gc.aafc.dina.extension.FieldExtensionValue;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Objects;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MaterialSampleService extends MessageProducingService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;
  private final AssociationValidator associationValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;

  private final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
    materialSampleValidationContext;
  private final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
    preparationValidationContext;

  private final CollectionHierarchicalDataDAO hierarchicalDataService;

  private final IdentifierTypeValueValidator identifierTypeValueValidator;
  private final MaterialSampleExtensionValueValidator materialSampleExtensionValueValidator;
  private final RestrictionExtensionValueValidator restrictionExtensionValueValidator;

  public MaterialSampleService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull MaterialSampleValidator materialSampleValidator,
    @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
    @NonNull AssociationValidator associationValidator,
    @NonNull CollectionHierarchicalDataDAO hierarchicalDataService,
    @NonNull MaterialSampleExtensionValueValidator materialSampleExtensionValueValidator,
    @NonNull RestrictionExtensionValueValidator restrictionExtensionValueValidator,
    IdentifierTypeValueValidator identifierTypeValueValidator,
    ApplicationEventPublisher eventPublisher
  ) {
    super(baseDAO, sv, MaterialSampleDto.TYPENAME, eventPublisher);
    this.materialSampleValidator = materialSampleValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.associationValidator = associationValidator;
    this.hierarchicalDataService = hierarchicalDataService;
    this.materialSampleExtensionValueValidator = materialSampleExtensionValueValidator;
    this.restrictionExtensionValueValidator = restrictionExtensionValueValidator;
    this.identifierTypeValueValidator = identifierTypeValueValidator;

    this.materialSampleValidationContext = CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
            .from(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    this.preparationValidationContext = CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
      .from(CollectionManagedAttribute.ManagedAttributeComponent.PREPARATION);
  }

  @Override
  public <T> List<T> findAll(
    @NonNull Class<T> entityClass,
    @NonNull PredicateSupplier<T> where,
    BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy,
    int startIndex,
    int maxResult,
    @NonNull Set<String> includes,
    @NonNull Set<String> relationships
  ) {

    log.debug("Relationships received: {}", relationships);
    // We can't fetch join materialSampleChildren without getting duplicates since it's a read-only list and we can't use the OrderColumn
    // This will let materialSampleChildren be lazy loaded
    Set<String> filteredRelationships = relationships.stream().filter( rel -> !rel.equalsIgnoreCase(MaterialSample.CHILDREN_COL_NAME)).collect(Collectors.toSet());

    List<T> all = super.findAll(entityClass, where, orderBy, startIndex, maxResult, includes, filteredRelationships);

    // sanity checks
    if (entityClass != MaterialSample.class || CollectionUtils.isEmpty(all)) {
      return all;
    }

    // augment information where required
    all.forEach(t -> {
      if (t instanceof MaterialSample ms) {
        try {
          if (includes.contains(MaterialSample.HIERARCHY_PROP_NAME)) {
            setHierarchy(ms);
          }
          if (includes.contains(MaterialSample.CHILDREN_COL_NAME)) {
            setChildrenOrdinal(ms);
          }
          if (relationships.contains(MaterialSample.ORGANISM_PROP_NAME)) {
            setTargetOrganismPrimaryScientificName(ms);
          }
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    });
    return all;
  }

  public void setHierarchy(MaterialSample sample) throws JsonProcessingException {
    sample.setHierarchy(hierarchicalDataService.getHierarchy(sample.getId()));
  }

  public void setChildrenOrdinal(MaterialSample sample) {
    List<ImmutableMaterialSample> sortedChildren = sample.getMaterialSampleChildren().stream()
            .sorted(Comparator.comparingInt(ImmutableMaterialSample::getId)).toList();

    for (int i = 0; i < sortedChildren.size(); i++) {
      sortedChildren.get(i).setOrdinal(i);
    }
  }

  public void setTargetOrganismPrimaryScientificName(MaterialSample sample) {

    if (CollectionUtils.isEmpty(sample.getOrganism())) {
      return;
    }

    // Filter the target organism or use all of them if target is not used (null)
    // Map to primary determination and make sure it's not null (should not happen but just in case)
    List<Determination> det = sample.getOrganism().stream()
      .filter(ms -> ms.getIsTarget() == null || ms.getIsTarget())
      .map(Organism::getPrimaryDetermination)
      .filter(Objects::nonNull).toList();

    String s = det.stream()
      .map( d -> {
        if (StringUtils.isNotBlank(d.getScientificName())) {
          return d.getScientificName();
        }
        return d.getVerbatimScientificName();
      }).collect(Collectors.joining("|"));
    sample.setTargetOrganismPrimaryScientificName(s);
  }

  @Override
  protected void preCreate(MaterialSample entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    linkAssociations(entity);
  }

  @Override
  protected void preUpdate(MaterialSample entity) {
    linkAssociations(entity);
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
    validateAssociations(entity);
    validateExtensionValues(entity);

    applyBusinessRule(entity, identifierTypeValueValidator);
  }

  private void validateManagedAttribute(MaterialSample entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
      materialSampleValidationContext);

    collectionManagedAttributeValueValidator.validate(entity, entity.getPreparationManagedAttributes(),
      preparationValidationContext);
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

  private void validateExtensionValues(@NonNull MaterialSample entity) {

    if (MapUtils.isNotEmpty(entity.getExtensionValues())) {
      for (String currExt : entity.getExtensionValues().keySet()) {
        entity.getExtensionValues().get(currExt).forEach((k, v) -> applyBusinessRule(
          entity.getUuid().toString(),
          FieldExtensionValue.builder().extKey(currExt).extFieldKey(k).value(v).build(),
          materialSampleExtensionValueValidator
        ));
      }
    }

    if (MapUtils.isNotEmpty(entity.getRestrictionFieldsExtension())) {
      for (String currExt : entity.getRestrictionFieldsExtension().keySet()) {
        entity.getRestrictionFieldsExtension().get(currExt).forEach((k, v) -> applyBusinessRule(
          entity.getUuid().toString(),
          FieldExtensionValue.builder().extKey(currExt).extFieldKey(k).value(v).build(),
          restrictionExtensionValueValidator
        ));
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
