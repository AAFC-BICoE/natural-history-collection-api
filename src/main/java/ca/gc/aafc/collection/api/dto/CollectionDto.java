package ca.gc.aafc.collection.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(Collection.class)
@JsonApiTypeForClass(CollectionDto.TYPENAME)
@TypeName(CollectionDto.TYPENAME)
public class CollectionDto implements JsonApiResource {

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

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private MultilingualDescription multilingualDescription;

  private String webpage;
  private String contact;
  private String address;
  private String remarks;

  @Builder.Default
  private Map<String, String> identifiers = Map.of();

  // -- Relationships --
  @JsonIgnore
  @ShallowReference
  private CollectionDto parentCollection;

  @JsonIgnore
  @ShallowReference
  private InstitutionDto institution;

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
