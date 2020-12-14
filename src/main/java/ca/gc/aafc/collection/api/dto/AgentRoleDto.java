package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.AgentRole;
import ca.gc.aafc.collection.api.entities.AgentRole.AgentRoleType;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RelatedEntity(AgentRole.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "agent-role")
public class AgentRoleDto {

  @JsonApiId
  private UUID uuid;

  private String createdBy;
  private OffsetDateTime createdOn;
  
  private AgentRoleType agentRoleType;
  private Map<String, String> name;  

}
