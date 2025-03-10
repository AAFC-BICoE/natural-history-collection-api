package ca.gc.aafc.collection.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

@AllArgsConstructor
@Getter
@JsonApiTypeForClass(IdentifierTypeDto.TYPE)
public class IdentifierTypeDto {

  public static final String TYPE = "identifier-type";

  @JsonApiId
  private final String id;

  private final TypedVocabularyElement.VocabularyElementType vocabularyElementType;

  private final MultilingualTitle multilingualTitle;

}
