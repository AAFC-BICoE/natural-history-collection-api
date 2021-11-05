package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionIdentifier;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(Collection.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = CollectionDto.TYPENAME)
public class CollectionDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "collection";
  
  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;
  
  private String group;

  private String name;

  private String code;

  @JsonApiRelation
  private InstitutionDto institution;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MultilingualDescription multilingualDescription;

  private String webpage;
  private String contact;
  private String address;
  private String remarks;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<CollectionIdentifier> identifiers = new ArrayList<>();

  @JsonApiRelation
  private CollectionDto parentCollection;

}
