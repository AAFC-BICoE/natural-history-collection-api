package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import io.crnk.core.resource.annotations.JsonApiId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@AllArgsConstructor
@Getter
@JsonApiTypeForClass(VocabularyDto.TYPE)
public class VocabularyDto {
  public static final String TYPE = "vocabulary";

  @JsonApiId
  private final String id;

  private final List<CollectionVocabularyConfiguration.CollectionVocabularyElement> vocabularyElements;

}
