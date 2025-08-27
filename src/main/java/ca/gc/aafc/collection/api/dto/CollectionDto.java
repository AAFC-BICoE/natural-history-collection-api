package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionIdentifier;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

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

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @Builder.Default
  private List<CollectionIdentifier> identifiers = new ArrayList<>();

  @ShallowReference
  private CollectionDto parentCollection;

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
