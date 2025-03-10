package ca.gc.aafc.collection.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.OrganismValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

@Log4j2
@Service
public class OrganismService extends DefaultDinaService<Organism> {

  private final OrganismValidator organismValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final Set<String> knownTaxonomicRanks;

  private static final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
    ORGANISM_VALIDATION_CONTEXT =
    CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
      .from(CollectionManagedAttribute.ManagedAttributeComponent.ORGANISM);

  private static final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
    DETERMINATION_VALIDATION_CONTEXT =
    CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
      .from(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION);

  public OrganismService(BaseDAO baseDAO,
                         SmartValidator sv,
                         OrganismValidator organismValidator,
                         CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator,
                         CollectionVocabularyConfiguration collectionVocabularyConfiguration) {
    super(baseDAO, sv);
    this.organismValidator = organismValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;

    knownTaxonomicRanks = collectionVocabularyConfiguration
      .getVocabularyByKey(CollectionVocabularyConfiguration.TAXONOMIC_RANK_KEY)
      .stream()
      .map(e -> e.getName().toLowerCase())
      .collect(Collectors.toSet());
  }

  @Override
  protected void preCreate(Organism entity) {
    // allow user provided UUID
    if (entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }
    entity.setGroup(standardizeGroupName(entity));
    setupDeterminations(entity);
  }

  @Override
  protected void preUpdate(Organism entity) {
    setupDeterminations(entity);
  }

  @Override
  public void validateBusinessRules(Organism entity) {
    applyBusinessRule(entity, organismValidator);
    validateOrganismManagedAttribute(entity);
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

  /**
   * From the provided determination extract the classification by matching
   * the known ranks from {@link CollectionVocabularyConfiguration} and the classification
   * path and ranks from {@link Determination.ScientificNameSourceDetails}.
   * @param primaryDetermination
   * @return
   */
  public Map<String, String> extractClassification(Determination primaryDetermination) {

    if (primaryDetermination == null) {
      return null;
    }

    Map<String, String> classification = new HashMap<>();
    if (primaryDetermination.getScientificNameDetails() != null) {
      String path = primaryDetermination.getScientificNameDetails().getClassificationPath();
      String ranks = primaryDetermination.getScientificNameDetails().getClassificationRanks();

      if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(ranks)) {
        String[] classificationNames = StringUtils.split(path, "|");
        String[] classificationRanks = StringUtils.split(ranks, "|");

        if (classificationNames.length == classificationRanks.length) {
          for (int i = 0; i < classificationNames.length; i++) {
            if (knownTaxonomicRanks.contains(classificationRanks[i])) {
              classification.put(classificationRanks[i], classificationNames[i]);
            }
          }
        } else {
          log.debug("classificationNames and classificationRanks arrays size are not the same");
        }
      }
    }
    return classification;
  }

  private void validateOrganismManagedAttribute(Organism organism) {
    if (MapUtils.isNotEmpty(organism.getManagedAttributes())) {
      collectionManagedAttributeValueValidator.validate(organism, organism.getManagedAttributes(),
        ORGANISM_VALIDATION_CONTEXT);
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
              determination.getManagedAttributes(), DETERMINATION_VALIDATION_CONTEXT);
        }
      });
    }
  }
}
