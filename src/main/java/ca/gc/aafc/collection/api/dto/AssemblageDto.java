package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RelatedEntity(Assemblage.class)
@JsonApiResource(type = AssemblageDto.TYPENAME)
@TypeName(AssemblageDto.TYPENAME)
public class AssemblageDto {

  public static final String TYPENAME = "assemblage";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = List.of();

  /**
   * Map of Managed attribute key to value object.
   */
  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> managedAttributes = Map.of();

  private MultilingualTitle multilingualTitle;
  private MultilingualDescription multilingualDescription;

}
