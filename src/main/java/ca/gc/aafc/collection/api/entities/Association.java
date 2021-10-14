package ca.gc.aafc.collection.api.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class Association {
  @EmbeddedId
  private AssociationKey primaryKey;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("sampleId")
  @JoinColumn(name = "sample_id", nullable = false, updatable = false)
  private MaterialSample sample;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("associatedSampleId")
  @JoinColumn(name = "associated_with_id", nullable = false, updatable = false)
  private MaterialSample associatedSample;

  @Size(max = 50)
  private String associationType;

  @Embeddable
  private static class AssociationKey implements Serializable {
    private Integer sampleId;
    private Integer associatedSampleId;
  }
}
