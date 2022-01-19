package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@RelatedEntity(Project.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = ProjectDto.TYPENAME)
@TypeName(ProjectDto.TYPENAME)
public class ProjectDto {
  
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
  
  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  private MultilingualDescription multilingualDescription;

}
