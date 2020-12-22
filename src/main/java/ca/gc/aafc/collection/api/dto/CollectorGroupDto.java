package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

@RelatedEntity(CollectorGroup.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "collector-group")
public class CollectorGroupDto {

  @JsonApiId
  private UUID uuid;

  private String createdBy;
  private OffsetDateTime createdOn;

  private String name;  

  private LinkedHashSet<UUID> agentIdentifiers;  

}
