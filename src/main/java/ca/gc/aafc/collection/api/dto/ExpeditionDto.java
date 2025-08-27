package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.entities.ExpeditionIdentifier;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import ca.gc.aafc.dina.dto.JsonApiResource;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@RelatedEntity(Expedition.class)
@JsonApiTypeForClass(ExpeditionDto.TYPENAME)
@TypeName(ExpeditionDto.TYPENAME)
public class ExpeditionDto implements JsonApiResource {

  public static final String TYPENAME = "expedition";

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
  private String geographicContext;

  @JsonApiExternalRelation(type = "person")
  private List<ExternalRelationDto> participants = new ArrayList<>();

  private MultilingualDescription multilingualDescription;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ExpeditionIdentifier> identifiers = new ArrayList<>();

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
