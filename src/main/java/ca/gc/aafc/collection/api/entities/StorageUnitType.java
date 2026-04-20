package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.type.SqlTypes;

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
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
public class StorageUnitType implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 150)
  private String name;

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

  private Boolean isInseperable;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "grid_layout_definition", columnDefinition = "jsonb")
  @Valid
  private StorageGridLayout gridLayoutDefinition;

}
