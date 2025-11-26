package ca.gc.aafc.collection.api.dto;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@JsonApiTypeForClass(CollectionSequenceGeneratorDto.TYPENAME)
@RelatedEntity(CollectionSequenceGenerationRequest.class)
public class CollectionSequenceGeneratorDto implements JsonApiResource {

  public static final String TYPENAME = "collection-sequence-generator";

  @JsonApiId
  private UUID collectionId;

  private Integer amount = 1;

  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.CREATE)
  private CollectionSequenceReserved result;

  // Retrieved from the collection
  private String group;

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return collectionId;
  }
}
