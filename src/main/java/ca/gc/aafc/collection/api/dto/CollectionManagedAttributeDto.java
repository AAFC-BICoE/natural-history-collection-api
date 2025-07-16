package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@RelatedEntity(CollectionManagedAttribute.class)
@Data
@JsonApiTypeForClass(CollectionManagedAttributeDto.TYPENAME)
@TypeName(CollectionManagedAttributeDto.TYPENAME)
public class CollectionManagedAttributeDto implements ca.gc.aafc.dina.dto.JsonApiResource {

  public static final String TYPENAME = "managed-attribute";

  @Id
  @PropertyName("id")
  @com.toedter.spring.hateoas.jsonapi.JsonApiId
  private UUID uuid;
  private String name;
  private String key;
  private TypedVocabularyElement.VocabularyElementType vocabularyElementType;
  private String unit;
  private CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent;
  private String[] acceptedValues;
  private OffsetDateTime createdOn;
  private String createdBy;
  private String group;
  private MultilingualDescription multilingualDescription;

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
