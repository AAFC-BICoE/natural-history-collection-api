package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

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

  @Type(type = "pgsql_enum")
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

  @Type(type = "string-array")
  private String[] dwcOtherCatalogNumbers;

  @Column(name = "material_sample_name")
  private String materialSampleName;

  // Preparation related

  @Column(name = "prepared_by")
  private UUID preparedBy;

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

  @Type(type = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Size(max = 250)
  @Column(name = "dwc_degree_of_establishment")
  private String dwcDegreeOfEstablishment;

  @Size(max = 50)
  private String barcode;

  private Boolean publiclyReleasable;

  @Size(max = 500)
  private String notPubliclyReleasableReason;

  @Type(type = "string-array")
  private String[] tags;

  @Type(type = "jsonb")
  @Valid
  private List<ScheduledActionDto> scheduledActions;

  @Type(type = "jsonb")
  @Valid
  private HostOrganism hostOrganism;

  @Column(name = "allow_duplicate_name")
  @NotNull
  @Builder.Default
  private Boolean allowDuplicateName = false;

  @Type(type = "jsonb")
  @Valid
  private List<ExtensionValue> restrictionFieldsExtension;

  @Builder.Default
  private Boolean isRestricted = false;

  @Size(max = 1000)
  private String restrictionRemarks;

}
