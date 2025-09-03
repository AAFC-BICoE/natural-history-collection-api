package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.entities.InstitutionIdentifier;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(Institution.class)
@JsonApiTypeForClass(InstitutionDto.TYPENAME)
@TypeName(InstitutionDto.TYPENAME)
public class InstitutionDto implements JsonApiResource {

  public static final String TYPENAME = "institution";

  @JsonApiId
  @PropertyName("id")
  @Id
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private MultilingualDescription multilingualDescription;
  private String webpage;

  private String address;

  private String remarks;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<InstitutionIdentifier> identifiers = new ArrayList<>();

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
