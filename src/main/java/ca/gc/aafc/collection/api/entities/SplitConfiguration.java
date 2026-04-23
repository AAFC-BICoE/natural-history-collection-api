package ca.gc.aafc.collection.api.entities;

import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import ca.gc.aafc.dina.entity.DinaEntityIdentifiableByName;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class SplitConfiguration implements DinaEntityIdentifiableByName {

  public enum Separator {
    SPACE(" "), DASH("-"), UNDERSCORE("_");
    private final String separatorChar;

    Separator(String separatorChar) {
      this.separatorChar = separatorChar;
    }

    public String getSeparatorChar() {
      return separatorChar;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @NotBlank
  private String name;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @NotNull
  private MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy;

  @Type(
    value = EnumArrayType.class,
    parameters = @Parameter(
      name = EnumArrayType.SQL_ARRAY_TYPE,
      value = "material_sample_type_enum"
    )
  )
  @Column(columnDefinition = "material_sample_type_enum[]")
  private MaterialSample.MaterialSampleType[] conditionalOnMaterialSampleTypes;

  @NotNull
  @Enumerated(EnumType.STRING)
  private MaterialSampleNameGeneration.CharacterType characterType;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Separator separator;

  @Enumerated(EnumType.STRING)
  private MaterialSample.MaterialSampleType materialSampleTypeCreatedBySplit;

}
