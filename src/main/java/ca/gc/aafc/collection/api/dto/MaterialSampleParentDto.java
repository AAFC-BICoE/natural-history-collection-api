package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.MaterialSampleParent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Resource that is only used to represent a parent material relationship.
 * It only contains the object internal identity (id) and external identify (uuid).
 */
@RelatedEntity(MaterialSampleParent.class)
@JsonApiResource(type = MaterialSampleParentDto.TYPE)
@Getter
@Setter
public class MaterialSampleParentDto {

  public static final String TYPE = "material-sample-parent";

  @JsonApiId
  private UUID uuid;
}
