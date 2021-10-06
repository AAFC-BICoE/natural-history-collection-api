package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Institution extends UserDescribedDinaEntity {

  @URL
  private String webpage;

  @Size(max = 500)
  private String address;

  @Size(max = 500)
  private String remarks;
}
