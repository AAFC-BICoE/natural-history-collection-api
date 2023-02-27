package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.AbstractMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.swing.text.html.Option;
import lombok.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class MaterialSampleIdentifierGenerator {

  private static final Pattern TRAILING_LETTERS_REGEX = Pattern.compile("([a-zA-Z]+)$");
  private static final Pattern TRAILING_NUMBERS_REGEX = Pattern.compile("(\\d+)$");

  private final MaterialSampleService materialSampleService;

  public MaterialSampleIdentifierGenerator(MaterialSampleService materialSampleService) {
    this.materialSampleService = materialSampleService;
  }

  public String generateNextIdentifier(UUID currentParentUUID, MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy strategy,
                                     MaterialSampleIdentifierGeneratorDto.CharacterType characterType) {

    // load current parent with hierarchy
    MaterialSample ms = materialSampleService.findOne(currentParentUUID, MaterialSample.class);
    try {
      materialSampleService.setHierarchy(ms);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    List<Integer> materialSampleIdsToLoad = new ArrayList<>();
    if(strategy == MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy.DIRECT_PARENT) {
      materialSampleIdsToLoad.add(ms.getId());
    } else if (strategy == MaterialSampleIdentifierGeneratorDto.IdentifierGenerationStrategy.TYPE_BASED) {
      materialSampleIdsToLoad.addAll( ms.getHierarchy().stream().map(
        MaterialSampleHierarchyObject::getId).toList());
    } else {
      throw new IllegalStateException("unknown strategy");
    }

    List<String> descendantNames = new ArrayList<>();
    findDescendantNames(materialSampleIdsToLoad, descendantNames, true);

    if(descendantNames.isEmpty()) {
      return startSeries(ms.getMaterialSampleName(), characterType);
    }

    // get of max of all children of all loaded material sample
    Optional<String>
      lastName = descendantNames.stream()
      .max(Comparator.naturalOrder());

    return generateNextIdentifier(lastName.orElse(null));
  }

  private void findDescendantNames(List<Integer> ids, List<String> materialSampleNameAccumulator, boolean recursive) {
    PredicateSupplier<MaterialSample> ps = (cb,  root, em) -> new Predicate[]{root.get("id").in(ids)};
    List<MaterialSample> materialSample = materialSampleService.findAll(MaterialSample.class,
      ps, null, 0, 500, Set.of(), Set.of(MaterialSample.CHILDREN_COL_NAME));

    List<Integer> childrenId = new ArrayList<>();

    for(MaterialSample currMs : materialSample) {
      if (currMs.getMaterialSampleChildren() != null) {
        for (AbstractMaterialSample currChild : currMs.getMaterialSampleChildren()) {
          childrenId.add(currChild.getId());
          materialSampleNameAccumulator.add(currChild.getMaterialSampleName());
        }
      }
    }

    if(recursive && !childrenId.isEmpty()) {
      findDescendantNames(childrenId, materialSampleNameAccumulator, true);
    }

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
