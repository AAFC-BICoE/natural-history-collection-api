package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSampleHierarchyObject extends HierarchicalObject {

  private DeterminationSummary targetOrganismPrimaryDetermination;

  public static MaterialSampleHierarchyObject fromRaw(MaterialSampleHierarchyObjectRaw raw, DeterminationSummary targetOrganismPrimaryDetermination) {
    MaterialSampleHierarchyObject matSampleObj = new MaterialSampleHierarchyObject();
    matSampleObj.setId(raw.getId());
    matSampleObj.setUuid(raw.getUuid());
    matSampleObj.setName(raw.getName());
    matSampleObj.setType(raw.getType());
    matSampleObj.setRank(raw.getRank());
    matSampleObj.setTargetOrganismPrimaryDetermination(targetOrganismPrimaryDetermination);
    return matSampleObj;
  }

  /**
   * The raw version is to simplify the integration with MyBatis
   */
  @Getter
  @Setter
  public static class MaterialSampleHierarchyObjectRaw extends HierarchicalObject {
    // Primary determination of the target organism
    private JsonNode targetOrganismPrimaryDetermination;
  }

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
