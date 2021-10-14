package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.dto.ScheduledActionDto;
import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NaturalIdCache
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
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

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = new ArrayList<>();

  @Column(name = "material_sample_name")
  private String materialSampleName;

  @Column(name = "prepared_by")
  private UUID preparedBy;

  private LocalDate preparationDate;

  @Type(type = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = new HashMap<>();

  @Type(type = "jsonb")
  @Valid
  private List<Determination> determination;

  @Type(type = "jsonb")
  private Organism organism;

  @Size(max = 500)
  @Column(name = "preparation_remarks")
  private String preparationRemarks;

  @Size(max = 250)
  @Column(name = "dwc_degree_of_establishment")
  private String dwcDegreeOfEstablishment;

  @Size(max = 250)
  private String host;

  @Size(max = 50)
  private String barcode;

  private Boolean publiclyReleasable;

  @Size(max = 500)
  private String notPubliclyReleasableReason;

  @Type(type = "string-array")
  private String[] tags;

  @Type(type = "jsonb")
  private List<ScheduledActionDto> scheduledActions;

  @Size(max = 255)
  private String filedAs;

  @Size(max = 250)
  private String preparationMethod;

}
