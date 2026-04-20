package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.type.SqlTypes;

import ca.gc.aafc.collection.api.dto.ScheduledActionDto;
import ca.gc.aafc.dina.entity.DinaEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NaturalIdCache
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public class AbstractMaterialSample implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @Enumerated(EnumType.STRING)
  private MaterialSample.MaterialSampleType materialSampleType;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Size(max = 25)
  private String dwcCatalogNumber;

  private String[] dwcOtherCatalogNumbers;

  // Represents the Primary ID
  @Column(name = "material_sample_name")
  private String materialSampleName;

  // Represents the alternative identifiers
  @JdbcTypeCode(SqlTypes.JSON)
  @NotNull
  @Builder.Default
  private Map<String, String> identifiers = Map.of();

  // Preparation related

  private LocalDate preparationDate;

  @Size(max = 1000)
  @Column(name = "preparation_remarks")
  private String preparationRemarks;

  @Size(max = 250)
  private String preservationType;

  @Size(max = 250)
  private String preparationFixative;

  @Size(max = 250)
  private String preparationMaterials;

  @Size(max = 250)
  private String preparationSubstrate;

  @JdbcTypeCode(SqlTypes.JSON)
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @JdbcTypeCode(SqlTypes.JSON)
  @NotNull
  @Builder.Default
  private Map<String, String> preparationManagedAttributes = Map.of();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "extension_values", columnDefinition = "jsonb")
  private Map<String, Map<String, String>> extensionValues = Map.of();

  @Size(max = 250)
  @Column(name = "dwc_degree_of_establishment")
  private String dwcDegreeOfEstablishment;

  @Size(max = 50)
  private String barcode;

  private Boolean publiclyReleasable;

  @Size(max = 500)
  private String notPubliclyReleasableReason;

  private String[] tags;

  @JdbcTypeCode(SqlTypes.JSON)
  @Valid
  private List<ScheduledActionDto> scheduledActions;

  @JdbcTypeCode(SqlTypes.JSON)
  @Valid
  private HostOrganism hostOrganism;

  @Column(name = "allow_duplicate_name")
  @NotNull
  @Builder.Default
  private Boolean allowDuplicateName = false;

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Map<String, String>> restrictionFieldsExtension;

  @Builder.Default
  private Boolean isRestricted = false;

  @Size(max = 1000)
  private String restrictionRemarks;

  private Boolean isBaseForSplitByType;

}
