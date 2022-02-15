package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@NaturalIdCache
public class MaterialSampleType implements DinaEntity {

  // The following UUIDs are inserted by Liquibase and considered stable
  public static final UUID MIXED_ORGANISMS_UUID = UUID.fromString("24d0a216-5967-4bb0-875a-47655e7c0ae8");
  public static final UUID ORGANISM_PART_UUID = UUID.fromString("6c2aedd0-220e-41de-b0e9-d36f0c6e09b9");
  public static final UUID WHOLE_ORGANISM_UUID = UUID.fromString("3a001690-7008-45be-b6a8-cc1808eefbc6");
  public static final UUID MOLECULAR_SAMPLE_UUID = UUID.fromString("3426a6db-99db-4f9d-9c26-eaed1d6906e5");

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
  @Size(max = 150)
  private String name;
}
