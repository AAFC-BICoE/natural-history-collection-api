package ca.gc.aafc.collection.api.entities;

import jakarta.persistence.Column;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;

import ca.gc.aafc.dina.entity.DinaEntity;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class Association implements DinaEntity {

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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sample_id", nullable = false)
  private MaterialSample sample;
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "associated_with_id", nullable = false)
  @NotNull
  private MaterialSample associatedSample;
  
  @Size(max = 50)
  @NotBlank
  private String associationType;

  @Size(max = 1000)
  private String remarks;

  /**
   * Use the group of the sample "owning" the relationship
   * @return
   */
  @Override
  public String getGroup() {
    if (sample != null) {
      return sample.getGroup();
    }
    return null;
  }
}
