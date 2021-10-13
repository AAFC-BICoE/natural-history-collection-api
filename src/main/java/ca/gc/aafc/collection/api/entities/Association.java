package ca.gc.aafc.collection.api.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@IdClass(Association.class)
public class Association implements Serializable {

  @Id
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sample_id", nullable = false, updatable = false)
  private MaterialSample sample;

  @Id
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "associated_with_id", nullable = false, updatable = false)
  private MaterialSample associatedSample;

  @Size(max = 50)
  private String associationType;

}
