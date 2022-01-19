package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

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
@RelatedEntity(PreparationType.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = PreparationTypeDto.TYPENAME)
@TypeName(PreparationTypeDto.TYPENAME)
public class PreparationTypeDto {

  public static final String TYPENAME = "preparation-type";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;
  
  private String group;

  private String name;

  private MultilingualDescription multilingualDescription;
}
