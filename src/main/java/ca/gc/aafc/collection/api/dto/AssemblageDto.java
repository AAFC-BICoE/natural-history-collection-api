package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@RelatedEntity(Assemblage.class)
@JsonApiTypeForClass(AssemblageDto.TYPENAME)
@TypeName(AssemblageDto.TYPENAME)
public class AssemblageDto implements JsonApiResource {

  public static final String TYPENAME = "assemblage";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  /**
   * Map of Managed attribute key to value object.
   */
  private Map<String, String> managedAttributes = Map.of();

  private MultilingualTitle multilingualTitle;
  private MultilingualDescription multilingualDescription;

  // -- External relationships --
  @JsonApiExternalRelation(type = "metadata")
  @JsonIgnore
  private List<ExternalRelationDto> attachment = List.of();

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
