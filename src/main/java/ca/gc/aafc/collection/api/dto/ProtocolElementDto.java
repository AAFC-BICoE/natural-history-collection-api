package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonApiResource(type = "protocol-element")
public class ProtocolElementDto {

  @JsonApiId
  private final String id;

  private final String term;

  private final TypedVocabularyElement.VocabularyElementType vocabularyElementType;

  private final MultilingualTitle multilingualTitle;

}
