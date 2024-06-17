package ca.gc.aafc.collection.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.collection.api.entities.AbstractMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.criteria.Predicate;

/**
 * Responsible to generate a material-sample name based on the hierarchy and a strategy.
 * The generated name is NOT guaranteed to be unique, it is simply representing what would be the next logical name.
 *
 * The algorithm is based on 2 components: the basename and the suffix where the basename is representing the part that should be fixed and
 * the suffix the part that should be incremented. Depending on the strategy they will be determined differently.
 *
 */
@Service
public class MaterialSampleIdentifierGenerator {

  private static final Pattern TRAILING_LETTERS_REGEX = Pattern.compile("([a-zA-Z]+)$");
  private static final Pattern TRAILING_NUMBERS_REGEX = Pattern.compile("(\\d+)$");

  private static final Integer MAX_MAT_SAMPLE = 500;
  private static final String IDENTIFIER_SEPARATOR = "-";

  private final MaterialSampleService materialSampleService;

  public MaterialSampleIdentifierGenerator(MaterialSampleService materialSampleService) {
    this.materialSampleService = materialSampleService;
  }

  /**
   *
   * @param currentParentUUID parent from which a new child is created
   * @param strategy strategy to use to find the new id
   * @param materialSampleType material sample type of the new child to be created
   * @param characterType if we need to create a new series, what should be used.
   * @return
   */
  public String generateNextIdentifier(UUID currentParentUUID,
                                       MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy,
                                       MaterialSample.MaterialSampleType materialSampleType,
                                       MaterialSampleNameGeneration.CharacterType characterType) {

    Objects.requireNonNull(strategy);
    Objects.requireNonNull(characterType);

    // load current parent with hierarchy
    MaterialSample ms = materialSampleService.findOne(currentParentUUID, MaterialSample.class);
    try {
      materialSampleService.setHierarchy(ms);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    IdentifierGenerationParameters params = switch (strategy) {
      case DIRECT_PARENT -> prepareDirectParentNameGeneration(ms);
      case TYPE_BASED -> prepareTypeBasedNameGeneration(ms, materialSampleType);
    };


    // if there is no descendant we need to start a new series
    if (params.descendantNames.isEmpty()) {
      return startSeries(params.basename, IDENTIFIER_SEPARATOR, characterType);
    }

    // if we reached the max we need to stop
    if (params.descendantNames.size() == MAX_MAT_SAMPLE) {
      throw new IllegalStateException("maximum number of descendant reached.");
    }

    // get of max of all children of all loaded material sample
    // max is applied of the last part of the identifier (after the last separator)
    Optional<String>
      lastName = params.descendantNames.stream().map(str -> StringUtils.substringAfterLast(str, IDENTIFIER_SEPARATOR))
      .max(Comparator.naturalOrder());

    return generateNextIdentifier(params.basename + IDENTIFIER_SEPARATOR + lastName.orElse("?"));
  }

  /**
   * Generates parameters for DirectParent strategy.
   * basename: name from the parent
   * descendantsName: names of all the children of the parent
   * @param currentParent the current parent
   * @return parameters
   */
  private static IdentifierGenerationParameters prepareDirectParentNameGeneration(
    MaterialSample currentParent) {
    return new IdentifierGenerationParameters(currentParent.getMaterialSampleName(),
      extractAllChildrenNames(currentParent));
  }

  /**
   * Generates parameters for TypeBased strategy.
   * basename: name from the first parent in the hierarchy that has a type different from the provided type.
   * descendantsName: names of all the children in the entire hierarchy (recursive) where the type is matching the provided type.
   *
   * @param currentParent the current parent
   * @param materialSampleType the material-sample type of the material-sample to be created with the provided identifier
   * @return parameters
   */
  private IdentifierGenerationParameters prepareTypeBasedNameGeneration(MaterialSample currentParent, MaterialSample.MaterialSampleType materialSampleType) {
    List<Integer> hierarchyIds = currentParent.getHierarchy().stream().map(
      MaterialSampleHierarchyObject::getId).toList();

    List<MaterialSample> hierarchyMaterialSample = findMaterialSampleById(hierarchyIds);
    List<String> descendantNames = new ArrayList<>();
    extractAllDescendantNames(hierarchyMaterialSample, descendantNames,  materialSampleType);

    return new IdentifierGenerationParameters(getBaseName(hierarchyMaterialSample, materialSampleType),
      descendantNames);
  }

  /**
   * Only extracts direct children names.
   * @param materialSample
   * @return
   */
  private static List<String> extractAllChildrenNames(MaterialSample materialSample) {
    List<String> materialSampleNameAccumulator = new ArrayList<>();
    if (materialSample.getMaterialSampleChildren() != null) {
      for (AbstractMaterialSample currChild : materialSample.getMaterialSampleChildren()) {
        materialSampleNameAccumulator.add(currChild.getMaterialSampleName());
      }
    }
    return materialSampleNameAccumulator;
  }

  private void extractAllDescendantNames(List<MaterialSample> materialSample, List<String> materialSampleNameAccumulator, MaterialSample.MaterialSampleType type) {
    List<Integer> childrenId = new ArrayList<>();
    for (MaterialSample currMs : materialSample) {
      if (currMs.getMaterialSampleChildren() != null) {
        for (AbstractMaterialSample currChild : currMs.getMaterialSampleChildren()) {
          if (type == null || type == currChild.getMaterialSampleType()) {
            childrenId.add(currChild.getId());
            materialSampleNameAccumulator.add(currChild.getMaterialSampleName());
          }
        }
      }
    }

    if (!childrenId.isEmpty()) {
      extractAllDescendantNames(findMaterialSampleById(childrenId), materialSampleNameAccumulator, type);
    }
  }

  /**
   * Get the basename from a list of material-sample representing the entire hierarchy and a type.
   * Basename is defined as the name of the first parent where the type is different from the provided type.
   *
   * @param materialSampleHierarchy the entire hierarchy as material-sample
   * @param type the material-sample type to be created with the generated identifier
   * @return
   */
  private static String getBaseName(List<MaterialSample> materialSampleHierarchy, MaterialSample.MaterialSampleType type) {
    return materialSampleHierarchy.stream()
      .filter(ms -> ms.getMaterialSampleType() != type)
      .max(Comparator.comparingInt(MaterialSample::getId))
      .map(MaterialSample::getMaterialSampleName)
      .orElse(null);
  }

  /**
   * Load all the material-sample from the id list.
   * Will eager-load children material-sample.
   * @param idList
   * @return
   */
  private List<MaterialSample> findMaterialSampleById(List<Integer> idList) {
    PredicateSupplier<MaterialSample> ps = (cb,  root, em) -> new Predicate[]{root.get("id").in(idList)};
    return materialSampleService.findAll(MaterialSample.class,
      ps, null, 0, MAX_MAT_SAMPLE, Set.of(), Set.of(MaterialSample.CHILDREN_COL_NAME));
  }

  /**
   * Start a new series depending on the character type.
   *
   * @param basename
   * @param separator
   * @param characterType
   * @return
   */
  private static String startSeries(String basename, String separator, MaterialSampleNameGeneration.CharacterType characterType) {
    return basename + separator + switch (characterType) {
      case NUMBER -> "1";
      case LOWER_LETTER -> "a";
      case UPPER_LETTER -> "A";
    };
  }

  /**
   * This function generates a next identifier based on the provided one.
   * There is no guarantee on the uniqueness.
   * @param providedIdentifier
   * @return
   */
  public String generateNextIdentifier(String providedIdentifier) {

    String currSuffix = "";
    String nextSuffix = "";

    //try letters
    Matcher currMatcher = TRAILING_LETTERS_REGEX.matcher(providedIdentifier);
    if (currMatcher.find()) {
      currSuffix = currMatcher.group(1);

      int matchingNumber = NumberLetterTranslator.toNumber(currSuffix);
      nextSuffix = NumberLetterTranslator.toLetter(matchingNumber + 1);

      // return lowercase but only if the entire suffix was lowercase
      if (StringUtils.isAllLowerCase(currSuffix)) {
        nextSuffix = nextSuffix.toLowerCase();
      }
    }

    //otherwise try numbers
    currMatcher = TRAILING_NUMBERS_REGEX.matcher(providedIdentifier);
    if (currMatcher.find()) {
      currSuffix = currMatcher.group(1);
      int nextValue = Integer.parseInt(currSuffix) + 1;

      // preserve 0 left padding if it was there
      nextSuffix = StringUtils.startsWith(currSuffix, "0") ?
          StringUtils.leftPad(Integer.toString(nextValue), currSuffix.length(), "0") :
          Integer.toString(nextValue);
    }

    return StringUtils.removeEnd(providedIdentifier, currSuffix) + nextSuffix;
  }

  /**
   * Record representing the parameters of the identifier generation.
   * @param basename
   * @param descendantNames
   */
  private record IdentifierGenerationParameters(String basename, List<String> descendantNames) {
  }
}
