package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.net.URL;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Institution extends UserDescribedDinaEntity {

  private URL webpage;

  @Size(max = 500)
  private String address;

  @Size(max = 500)
  private String remarks;
}
