package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSampleHierarchyObject extends HierarchicalObject {

  private DeterminationSummary targetOrganismPrimaryDetermination;

  /**
   * Summarized version of {@link ca.gc.aafc.collection.api.entities.Determination}
   */
  @Getter
  @Setter
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class DeterminationSummary {
    private String verbatimScientificName;
    private String scientificName;
    private ScientificNameSourceDetailsSummary scientificNameDetails;
  }

  /**
   * Summarized version of {@link ca.gc.aafc.collection.api.entities.Determination.ScientificNameSourceDetails}
   */
  @Getter
  @Setter
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class ScientificNameSourceDetailsSummary {
    private String currentName;
    private Boolean isSynonym = false;
  }
}
