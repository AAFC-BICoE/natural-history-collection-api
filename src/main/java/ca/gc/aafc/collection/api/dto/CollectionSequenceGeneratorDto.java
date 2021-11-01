package ca.gc.aafc.collection.api.dto;

import javax.persistence.Id;

import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonApiResource(type = CollectionSequenceGeneratorDto.TYPENAME)
@TypeName(value = CollectionSequenceGeneratorDto.TYPENAME)
public class CollectionSequenceGeneratorDto {

  public static final String TYPENAME = "collection-sequence";

  @Id
  @JsonApiId
  private Integer id;

  private Integer collectionId;

  private Integer amount = 1;

  private CollectionSequenceReserved result;
}
