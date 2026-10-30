package ca.gc.aafc.collection.api.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Coverage;
import ca.gc.aafc.dina.dto.BaseDatasetDto.DatasetType;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Project;
import ca.gc.aafc.dina.dto.BaseDatasetDto.KeywordSet;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Methods;
import ca.gc.aafc.dina.dto.BaseDatasetDto.UsageRights;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
public class Dataset implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @Size(max = 100)
  private String datasetVersion;

  private LocalDate publicationDate;

  @NotBlank
  @Column(name = "_group")
  private String group;

  @Version
  private Long resourceVersion;

  @NotNull
  @Enumerated(EnumType.STRING)
  private DatasetType datasetType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> query;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  @Valid
  @Builder.Default
  private List<AgentRoles> agentRoles = List.of();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "multilingual_title")
  @Valid
  protected MultilingualTitle multilingualTitle;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "multilingual_description")
  @Valid
  private MultilingualDescription multilingualDescription;
  
  @JdbcTypeCode(SqlTypes.JSON)
  private UsageRights usageRights;

  @JdbcTypeCode(SqlTypes.JSON)
  @Builder.Default
  private List<KeywordSet> keywordSets = List.of();

  @JdbcTypeCode(SqlTypes.JSON)
  private Coverage coverage;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Methods methods;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Project project;

  @UpdateTimestamp
  @Column(name = "last_updated_on")
  private Instant lastUpdatedOn;

  @Column(insertable = false, updatable = false)
  @Generated(event = EventType.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(updatable = false)
  private String createdBy;

}
