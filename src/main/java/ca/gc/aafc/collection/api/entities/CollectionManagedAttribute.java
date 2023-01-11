package ca.gc.aafc.collection.api.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.gc.aafc.dina.entity.ManagedAttribute;

import ca.gc.aafc.dina.i18n.MultilingualTitle;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "managed_attribute")
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@RequiredArgsConstructor
@NaturalIdCache
@AttributeOverride(name = "name", column = @Column(name = "name", updatable = false))
public class CollectionManagedAttribute extends UserDescribedDinaEntity implements ManagedAttribute {

  public enum ManagedAttributeComponent {
    COLLECTING_EVENT,
    MATERIAL_SAMPLE,
    DETERMINATION,
    ASSEMBLAGE;

    public static ManagedAttributeComponent fromString(String s) {
      for (ManagedAttributeComponent source : ManagedAttributeComponent.values()) {
        if (source.name().equalsIgnoreCase(s)) {
          return source;
        }
      }
      return null;
    }
  }

  @NotBlank
  @Column(name = "_group")
  @Size(max = 50)
  private String group;

  @NotBlank
  @Size(max = 50)
  @Column(updatable = false)
  private String key;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private VocabularyElementType vocabularyElementType;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "component")
  private ManagedAttributeComponent managedAttributeComponent;

  @Type(type = "string-array")
  @Column(columnDefinition = "text[]")
  private String[] acceptedValues;

  @Override
  public String getTerm() {
    return null;
  }

  @Override
  public MultilingualTitle getMultilingualTitle() {
    return null;
  }

}
