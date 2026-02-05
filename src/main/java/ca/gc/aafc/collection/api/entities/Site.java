package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Site extends UserDescribedDinaEntity {

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  private LocalDate startDate;
  private LocalDate endDate;

  @Size(max = 50)
  private String code;
}
