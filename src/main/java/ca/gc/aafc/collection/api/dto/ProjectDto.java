package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@RelatedEntity(Project.class)
@JsonApiTypeForClass(ProjectDto.TYPENAME)
@TypeName(ProjectDto.TYPENAME)
public class ProjectDto implements JsonApiResource {
  
  public static final String TYPENAME = "project";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;
  
  private String group;

  private String name;

  private LocalDate startDate;
  private LocalDate endDate;
  private String status;

  private List<AgentRoles> contributors = List.of();

  @JsonApiExternalRelation(type = "metadata")
  @JsonIgnore
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  private MultilingualDescription multilingualDescription;

  private Map<String, Map<String, String>> extensionValues = Map.of();

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }
}
