package ca.gc.aafc.collection.api.entities;

import java.util.Map;
import lombok.Builder;

/**
 * IdentifiableEntity is the future name of Organism
 */
@Builder
public class IdentifiableEntitySummary {

  private Map<String, String> managedAttributes;

  private String lifeStage;
  private String sex;

  private String dwcVernacularName;

  private Determination primaryDetermination;

}
