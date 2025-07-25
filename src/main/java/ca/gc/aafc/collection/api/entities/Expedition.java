package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import ca.gc.aafc.dina.entity.AgentRoles;

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
  
  @Type(type = "list-array")
  @Column(name = "participants", columnDefinition = "uuid[]")
  private List<UUID> participants = new ArrayList<>();

}
