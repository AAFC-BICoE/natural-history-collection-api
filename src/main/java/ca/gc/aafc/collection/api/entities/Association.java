package ca.gc.aafc.collection.api.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class Association {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sample_id", nullable = false)
  private MaterialSample sample;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "associated_with_id", nullable = false)
  private MaterialSample associatedSample;

  @Size(max = 50)
  private String associationType;

  @Size(max = 1000)
  private String remarks;

}
