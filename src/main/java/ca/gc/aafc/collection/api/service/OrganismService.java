package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.OrganismEntity;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.OrganismValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class OrganismService extends DefaultDinaService<OrganismEntity> {

  private final OrganismValidator organismValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;

  public OrganismService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, OrganismValidator organismValidator,
      CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator) {
    super(baseDAO, sv);
    this.organismValidator = organismValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
  }

  @Override
  protected void preCreate(OrganismEntity entity) {
    entity.setUuid(UUID.randomUUID());
    setupDeterminations(entity);
  }

  @Override
  protected void preUpdate(OrganismEntity entity) {
    setupDeterminations(entity);
  }

  @Override
  public void validateBusinessRules(OrganismEntity entity) {
    applyBusinessRule(entity, organismValidator);
    validateDeterminationManagedAttribute(entity);
  }

  /**
   * For each organism, if it contains a single determination
   * it will automatically set it to the primary determination.
   *
   * @param organism the organism
   */
  private void setupDeterminations(OrganismEntity organism) {
    // Check to see if one determination is present and is currently not primary.
    if (CollectionUtils.size(organism.getDetermination()) == 1 &&
        BooleanUtils.isFalse(organism.getDetermination().get(0).getIsPrimary())) {
      organism.getDetermination().get(0).setIsPrimary(true);
    }
  }

  private void validateDeterminationManagedAttribute(OrganismEntity organism) {
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
  }
}
