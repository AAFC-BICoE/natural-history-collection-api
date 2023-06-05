package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import javax.validation.Valid;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NaturalIdCache
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public abstract class UserDescribedDinaEntity implements DinaEntity {

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

  @Type(type = "jsonb")
  @Column(name = "multilingual_description")
  @Valid
  private MultilingualDescription multilingualDescription;

}
