package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@AllArgsConstructor
@Getter
@JsonApiTypeForClass(ProtocolElementDto.TYPENAME)
public class ProtocolElementDto implements JsonApiResource {

  public static final String TYPENAME = "protocol-element";

  @JsonApiId
  private final String id;

  private final String term;

  private final TypedVocabularyElement.VocabularyElementType vocabularyElementType;

  private final MultilingualTitle multilingualTitle;

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    //id is a string
    return null;
  }
}
