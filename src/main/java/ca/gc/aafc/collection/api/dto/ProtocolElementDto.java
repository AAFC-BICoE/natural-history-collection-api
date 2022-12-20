package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.ProtocolElementConfiguration;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@JsonApiResource(type = "protocol-element")
public class ProtocolElementDto {

  @JsonApiId
  private final String id;

  private final List<ProtocolElementConfiguration.ProtocolElement> protocolElements;
}
