package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Organism;
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
public class OrganismService extends DefaultDinaService<Organism> {

  private final OrganismValidator organismValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext validationContext;

  public OrganismService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, OrganismValidator organismValidator,
      CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator) {
    super(baseDAO, sv);
    this.organismValidator = organismValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.validationContext = CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
            .from(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION);
  }

  @Override
  protected void preCreate(Organism entity) {
    // allow user provided UUID
    if(entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID());
    }

    setupDeterminations(entity);
  }

  @Override
  protected void preUpdate(Organism entity) {
    setupDeterminations(entity);
  }

  @Override
  public void validateBusinessRules(Organism entity) {
    applyBusinessRule(entity, organismValidator);
    validateDeterminationManagedAttribute(entity);
  }

  /**
   * For each organism, if it contains a single determination
   * it will automatically set it to the primary determination.
   *
   * @param organism the organism
   */
  private void setupDeterminations(Organism organism) {
    // Check to see if one determination is present and is currently not primary.
    if (CollectionUtils.size(organism.getDetermination()) == 1 &&
        !BooleanUtils.isTrue(organism.getDetermination().get(0).getIsPrimary())) {
      organism.getDetermination().get(0).setIsPrimary(true);
    }
  }

  private void validateDeterminationManagedAttribute(Organism organism) {
    // An organism can have multiple determinations, go through each of them and validate.
    if (CollectionUtils.isNotEmpty(organism.getDetermination())) {
      organism.getDetermination().forEach(determination -> {
        if (determination.getManagedAttributes() != null) {
          collectionManagedAttributeValueValidator.validate(
              organism.getUuid().toString() + StringUtils.defaultString(determination.getScientificName()),
              determination,
              determination.getManagedAttributes(), validationContext);
        }
      });
    }
  }
}
