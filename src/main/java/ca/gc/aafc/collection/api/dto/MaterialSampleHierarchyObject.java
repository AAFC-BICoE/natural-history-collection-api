package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MaterialSampleHierarchyObject extends HierarchicalObject {

  private String targetOrganismDetermination;

}
