package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import ca.gc.aafc.dina.entity.DinaEntity;

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
public class Organism implements DinaEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Type(type = "jsonb")
  @Valid
  private List<Determination> determination;

  @Size(max = 50)
  private String lifeStage;

  @Size(max = 25)
  private String sex;

  @Size(max = 50)
  private String substrate;

  @Size(max = 1000)
  private String remarks;

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

}
