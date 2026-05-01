package ca.gc.aafc.collection.api.entities;

import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Expedition extends UserDescribedDinaEntity {

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  private LocalDate startDate;
  private LocalDate endDate;

  @Size(max = 250)
  private String geographicContext;

  @Column(name = "participants", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> participants = new ArrayList<>();
}
