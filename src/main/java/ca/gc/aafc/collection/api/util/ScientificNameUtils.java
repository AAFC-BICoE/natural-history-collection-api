package ca.gc.aafc.collection.api.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;

public final class ScientificNameUtils {

  private ScientificNameUtils() {
    // utility class
  }

  /**
   *
   * @param orgList list of {@link Organism}
   * @return
   */
  public static String extractTargetOrganismPrimaryScientificName(List<Organism> orgList) {

    // Filter the target organism or use all of them if target is not used (null)
    // Map to primary determination and make sure it's not null (should not happen but just in case)
    List<Determination> det = orgList.stream()
      .filter(org -> org.getIsTarget() == null || org.getIsTarget())
      .map(Organism::getPrimaryDetermination)
      .filter(Objects::nonNull).toList();

    return det.stream()
      .map(d -> chooseScientificName(d.getScientificName(), d.getVerbatimScientificName()))
      .collect(Collectors.joining("|"));
  }

  public static String extractEffectiveScientificName(List<MaterialSampleHierarchyObject> hierarchy) {
    Optional<List<MaterialSampleHierarchyObject.DeterminationSummary>> effectiveDetermination =
      hierarchy.stream()
        .map(MaterialSampleHierarchyObject::getOrganismPrimaryDetermination)
        .filter(CollectionUtils::isNotEmpty)
        .findFirst();

    if(effectiveDetermination.isEmpty()) {
      return "";
    }

    return effectiveDetermination.get().stream()
      .map(d -> chooseScientificName(d.getScientificName(), d.getVerbatimScientificName()))
      .collect(Collectors.joining("|"));

  }

  private static String chooseScientificName(String scientificName, String verbatimScientificName) {
    if (StringUtils.isNotBlank(scientificName)) {
      return scientificName;
    }
    return verbatimScientificName;
  }

}
