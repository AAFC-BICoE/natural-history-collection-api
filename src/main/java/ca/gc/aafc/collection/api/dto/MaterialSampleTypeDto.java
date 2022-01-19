package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.dina.dto.RelatedEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

@Data
@RelatedEntity(MaterialSampleType.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = MaterialSampleTypeDto.TYPENAME)
@TypeName(MaterialSampleTypeDto.TYPENAME)
public class MaterialSampleTypeDto {

  public static final String TYPENAME = "material-sample-type";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;

  private String name;
}
