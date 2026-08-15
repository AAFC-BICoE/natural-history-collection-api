package ca.gc.aafc.collection.api.entities;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * IdentifiableEntity is the future name of Organism
 */
@Builder
@Getter
public class IdentifiableEntitySummary implements Serializable {

  private Map<String, String> managedAttributes;

  private String lifeStage;
  private String sex;

  private String dwcVernacularName;

  private DeterminationSummary primaryDetermination;

}
