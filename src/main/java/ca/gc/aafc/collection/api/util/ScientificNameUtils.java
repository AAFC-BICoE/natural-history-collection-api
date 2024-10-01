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

/**
 * Utility class for handling scientific names
 */
public final class ScientificNameUtils {

  private ScientificNameUtils() {
    // utility class
  }

  /**
   * From a list of organism(s), return the scientificName (or verbatim if there is scientificName)
   * of the primary determination of the target organism (if there is one).
   * @param orgList list of {@link Organism}
   * @return the target organism primary scientific name or null if the list is empty
   */
  public static String extractTargetOrganismPrimaryScientificName(List<Organism> orgList) {

    if (CollectionUtils.isEmpty(orgList)) {
      return null;
    }

    // Filter the target organism or use all of them if target is not used (null)
    // Map to primary determination and make sure it's not null (should not happen but just in case)
    List<Determination> det = orgList.stream()
      .filter(org -> org.getIsTarget() == null || org.getIsTarget())
      .map(Organism::getPrimaryDetermination)
      .filter(Objects::nonNull).toList();

    return det.stream()
      .map(d -> pickScientificName(d.getScientificName(), d.getVerbatimScientificName()))
      .collect(Collectors.joining("|"));
  }

  /**
   * From an ordered list of {@link MaterialSampleHierarchyObject} representing the hierarchy from the
   * current materials-sample to the root, find the first determination and return
   * the scientificName (or verbatim if there is scientificName).
   * @param hierarchy
   * @return the scientificName or null
   */
  public static String extractEffectiveScientificName(List<MaterialSampleHierarchyObject> hierarchy) {

    if (CollectionUtils.isEmpty(hierarchy)) {
      return null;
    }

    Optional<List<MaterialSampleHierarchyObject.DeterminationSummary>> effectiveDetermination =
      hierarchy.stream()
        .map(MaterialSampleHierarchyObject::getOrganismPrimaryDetermination)
        .filter(CollectionUtils::isNotEmpty)
        .findFirst();

    if (effectiveDetermination.isEmpty()) {
      return null;
    }

    return effectiveDetermination.get().stream()
      .map(d -> pickScientificName(d.getScientificName(), d.getVerbatimScientificName()))
      .collect(Collectors.joining("|"));
  }

  /**
   * Returns scientificName if not blank or verbatimScientificName otherwise.
   * @param scientificName
   * @param verbatimScientificName
   * @return
   */
  private static String pickScientificName(String scientificName, String verbatimScientificName) {
    if (StringUtils.isNotBlank(scientificName)) {
      return scientificName;
    }
    return verbatimScientificName;
  }
}
