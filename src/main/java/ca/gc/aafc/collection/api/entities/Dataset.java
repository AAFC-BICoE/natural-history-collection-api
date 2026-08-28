package ca.gc.aafc.collection.api.entities;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;

import ca.gc.aafc.dina.dto.DatasetDto.DatasetType;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import io.hypersistence.utils.hibernate.type.json.JsonType;
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

  @NotBlank
  @Column(name = "_group")
  private String group;

  @Version
  private Long resourceVersion;

  @NotNull
  @Enumerated(EnumType.STRING)
  private DatasetType datasetType;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  @Valid
  @Builder.Default
  private List<AgentRoles> agentRoles = List.of();

  @Type(JsonType.class)
  @Column(name = "multilingual_title")
  @Valid
  protected MultilingualTitle multilingualTitle;

  @Type(JsonType.class)
  @Column(name = "multilingual_description")
  @Valid
  private MultilingualDescription multilingualDescription;

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
