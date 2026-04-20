package ca.gc.aafc.collection.api.entities;

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
  @NotNull
  private MaterialSample associatedSample;
  
  @Size(max = 50)
  @NotBlank
  private String associationType;

  @Size(max = 1000)
  private String remarks;

}
