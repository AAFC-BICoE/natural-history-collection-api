package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "organism")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@NaturalIdCache
public class Organism implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  private UUID uuid;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  // null allowed to represent that the concept of target is not used
  private Boolean isTarget;

  @Type(JsonType.class)
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Type(JsonType.class)
  @Valid
  private List<Determination> determination;

  @Size(max = 50)
  private String lifeStage;

  @Size(max = 256)
  private String sex;

  @Size(max = 100)
  private String dwcVernacularName;

  @Size(max = 1000)
  private String remarks;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  /**
   * Return the Primary Determination.
   * If the entity is in invalid state and there is more than 1 primary determination, the first
   * one found will be returned.
   * @return Primary Determination or null if none
   */
  public Determination getPrimaryDetermination() {
    if (CollectionUtils.isEmpty(determination)) {
      return null;
    }
    return determination.stream().filter(d -> d.getIsPrimary() != null && d.getIsPrimary()).findFirst()
      .orElse(null);
  }


  /**
   * Count the number of primary Determination.
   * If there is no Determination 0 will be returned.
   *
   * @return the number of primary determination or 0 if there is no determinations
   */
  public long countPrimaryDetermination() {
    if (CollectionUtils.isEmpty(determination)) {
      return 0;
    }
    return determination.stream().filter(d -> d.getIsPrimary() != null && d.getIsPrimary()).count();
  }

  /**
   * Count the number of filedAs Determination.
   * If there is no Determination 0 will be returned.
   *
   * @return the number of filedAs determination or 0 if there is no determinations
   */
  public long countFiledAsDetermination() {
    if (CollectionUtils.isEmpty(determination)) {
      return 0;
    }
    return determination.stream().filter(d -> d.getIsFiledAs() != null && d.getIsFiledAs()).count();
  }

}
