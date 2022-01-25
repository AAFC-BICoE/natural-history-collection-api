package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(CollectionMethod.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = CollectionMethodDto.TYPENAME)
@TypeName(CollectionMethodDto.TYPENAME)
public class CollectionMethodDto {

  public static final String TYPENAME = "collection-method";

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
