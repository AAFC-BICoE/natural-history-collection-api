package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import java.time.Instant;
import java.time.OffsetDateTime;

import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.Dataset;
import ca.gc.aafc.dina.dto.DinaDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@RelatedEntity(Dataset.class)
@JsonApiTypeForClass(DatasetDto.TYPENAME)
@TypeName(ExpeditionDto.TYPENAME)
public class DatasetDto extends ca.gc.aafc.dina.dto.DatasetDto implements DinaDto {

  public static final String TYPENAME = "dataset";

  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.UPDATE)
  private Long resourceVersion;

  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.UPDATE)
  private Instant lastUpdatedOn;
  
  private OffsetDateTime createdOn;
  private String createdBy;

  @JsonApiId
  @Id
  @PropertyName("id")
  @Override
  public UUID getUuid() {
    return uuid;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }
}
