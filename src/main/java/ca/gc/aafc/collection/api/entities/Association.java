package ca.gc.aafc.collection.api.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import io.crnk.core.resource.annotations.JsonApiRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class Association {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "associated_with_id")
  private ImmutableMaterialSample assignedWith;
  
  @Size(max = 50)
  private String associationType;
}
