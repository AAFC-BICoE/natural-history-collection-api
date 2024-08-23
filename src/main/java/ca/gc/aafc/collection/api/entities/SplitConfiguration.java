package ca.gc.aafc.collection.api.entities;

import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import ca.gc.aafc.dina.entity.DinaEntityIdentifiableByName;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@TypeDef(name = "material-sample-type-array", typeClass = EnumArrayType.class,
  defaultForType = MaterialSample.MaterialSampleType[].class,
  parameters = {@Parameter(name = EnumArrayType.SQL_ARRAY_TYPE, value = "material_sample_type_enum")})
public class SplitConfiguration implements DinaEntityIdentifiableByName {

  public enum Separator {SPACE, DASH, UNDERSCORE}

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

  @Type(type = "material-sample-type-array")
  private MaterialSample.MaterialSampleType[] conditionalOnMaterialSampleTypes;

  @NotNull
  @Enumerated(EnumType.STRING)
  private MaterialSampleNameGeneration.CharacterType characterType;

  @NotNull
  @Size(max = 1)
  private Separator separator;

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private MaterialSample.MaterialSampleType materialSampleTypeCreatedBySplit;

}
