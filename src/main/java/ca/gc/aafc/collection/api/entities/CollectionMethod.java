package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class CollectionMethod extends UserDescribedDinaEntity {

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

}
