package ca.gc.aafc.collection.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;

import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonApiResource(type = "collection-sequence", patchable = false, readable = false, deletable = false, sortable = false, filterable = false)
public class CollectionSequenceGeneratorDto {
  private Integer collectionId;

  private Integer amount = 1;

  private CollectionSequenceReserved result;
}
