package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Id;

import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.dto.RelatedEntity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@RelatedEntity(Organism.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = MaterialSampleTypeDto.TYPENAME)
@TypeName(MaterialSampleTypeDto.TYPENAME)
public class OrganismDto {
  public static final String TYPENAME = "organism";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<Determination> determination;

  private String lifeStage;
  private String sex;
  private String substrate;
  private String remarks;
}
