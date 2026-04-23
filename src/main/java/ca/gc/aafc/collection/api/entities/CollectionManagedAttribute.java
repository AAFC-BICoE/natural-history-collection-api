package ca.gc.aafc.collection.api.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
    ORGANISM,
    DETERMINATION,
    PREPARATION,
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
  @Enumerated(EnumType.STRING)
  @Type(PostgreSQLEnumType.class)
  @Column(name = "type")
  private VocabularyElementType vocabularyElementType;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Type(PostgreSQLEnumType.class)
  @Column(name = "component")
  private ManagedAttributeComponent managedAttributeComponent;

  @Column(columnDefinition = "text[]")
  private String[] acceptedValues;

  @Size(max = 50)
  private String unit;

  @Override
  public String getTerm() {
    return null;
  }

  @Override
  public MultilingualTitle getMultilingualTitle() {
    return null;
  }

}
