package ca.gc.aafc.collection.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.AbstractMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.criteria.Predicate;

@Service
public class MaterialSampleIdentifierGenerator {

  private static final Pattern TRAILING_LETTERS_REGEX = Pattern.compile("([a-zA-Z]+)$");
  private static final Pattern TRAILING_NUMBERS_REGEX = Pattern.compile("(\\d+)$");

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
  public String generateNextIdentifier(UUID currentParentUUID, MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy strategy,
                                     MaterialSample.MaterialSampleType materialSampleType,
                                     MaterialSampleIdentifierGeneratorDto.CharacterType characterType) {

    // load current parent with hierarchy
    MaterialSample ms = materialSampleService.findOne(currentParentUUID, MaterialSample.class);
    try {
      materialSampleService.setHierarchy(ms);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    String basename = "";

    List<String> descendantNames = new ArrayList<>();
    if(strategy == MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy.DIRECT_PARENT) {
      descendantNames.addAll(extractAllChildrenNames(List.of(ms.getParentMaterialSample())));
      basename = ms.getMaterialSampleName();
    } else if (strategy == MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy.TYPE_BASED) {
      List<Integer> hierarchyIds = ms.getHierarchy().stream().map(
        MaterialSampleHierarchyObject::getId).toList();
      PredicateSupplier<MaterialSample> ps = (cb,  root, em) -> new Predicate[]{root.get("id").in(hierarchyIds)};
      List<MaterialSample> hierarchyMaterialSample = materialSampleService.findAll(MaterialSample.class,
        ps, null, 0, 500, Set.of(), Set.of(MaterialSample.CHILDREN_COL_NAME));
      basename = getBaseName(hierarchyMaterialSample, materialSampleType);
      extractAllDescendantNames(hierarchyMaterialSample, descendantNames,  materialSampleType);
    } else {
      throw new IllegalStateException("unknown strategy");
    }

    // if there is no descendant we need to start a new series
    if(descendantNames.isEmpty()) {
      return startSeries(basename, characterType);
    }

    // get of max of all children of all loaded material sample
    // max is applied of the last part of the identifier (after the last separator)
    Optional<String>
      lastName = descendantNames.stream().map(str -> StringUtils.substringAfterLast(str, IDENTIFIER_SEPARATOR))
      .max(Comparator.naturalOrder());

    return generateNextIdentifier(basename + IDENTIFIER_SEPARATOR + lastName.orElse("?"));
  }

  /**
   * Only extracts direct children names.
   * @param materialSample
   * @return
   */
  private List<String> extractAllChildrenNames(List<MaterialSample> materialSample) {
    List<String> materialSampleNameAccumulator = new ArrayList<>();
    for (MaterialSample currMs : materialSample) {
      if (currMs.getMaterialSampleChildren() != null) {
        for (AbstractMaterialSample currChild : currMs.getMaterialSampleChildren()) {
          materialSampleNameAccumulator.add(currChild.getMaterialSampleName());
        }
      }
    }
    return materialSampleNameAccumulator;
  }

  private void extractAllDescendantNames(List<MaterialSample> materialSample, List<String> materialSampleNameAccumulator, MaterialSample.MaterialSampleType type) {
    List<Integer> childrenId = new ArrayList<>();
    for(MaterialSample currMs : materialSample) {
      if (currMs.getMaterialSampleChildren() != null) {
        for (AbstractMaterialSample currChild : currMs.getMaterialSampleChildren()) {
          if(type == null || type == currChild.getMaterialSampleType()) {
            childrenId.add(currChild.getId());
            materialSampleNameAccumulator.add(currChild.getMaterialSampleName());
          }
        }
      }
    }

    if(!childrenId.isEmpty()) {
      PredicateSupplier<MaterialSample> ps = (cb,  root, em) -> new Predicate[]{root.get("id").in(childrenId)};
      List<MaterialSample> childrenMaterialSample = materialSampleService.findAll(MaterialSample.class,
        ps, null, 0, 500, Set.of(), Set.of(MaterialSample.CHILDREN_COL_NAME));
      extractAllDescendantNames(childrenMaterialSample, materialSampleNameAccumulator, type);
    }
  }

  private String getBaseName(List<MaterialSample> materialSampleHierarchy, MaterialSample.MaterialSampleType type) {
    return materialSampleHierarchy.stream()
      .filter(ms -> ms.getMaterialSampleType() != type)
      .max(Comparator.comparingInt(MaterialSample::getId))
      .map(MaterialSample::getMaterialSampleName)
      .orElse(null);
  }

  private String startSeries(String current, MaterialSampleIdentifierGeneratorDto.CharacterType characterType) {
    return current + switch (characterType) {
      case NUMBER -> "-1";
      case LOWER_LETTER -> "-a";
      case UPPER_LETTER -> "-A";
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
    if(currMatcher.find()) {
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
    if(currMatcher.find()) {
      currSuffix = currMatcher.group(1);
      int nextValue = Integer.parseInt(currSuffix) + 1;

      // preserve 0 left padding if it was there
      nextSuffix = StringUtils.startsWith(currSuffix, "0") ?
          StringUtils.leftPad(Integer.toString(nextValue), currSuffix.length(), "0") :
          Integer.toString(nextValue);
    }

    return StringUtils.removeEnd(providedIdentifier, currSuffix) + nextSuffix;
  }
}
