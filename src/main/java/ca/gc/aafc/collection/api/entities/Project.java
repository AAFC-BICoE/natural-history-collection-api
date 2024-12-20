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
public class Project extends UserDescribedDinaEntity {
  
  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  private LocalDate startDate;
  private LocalDate endDate;

  @Size(max = 50)
  private String status;

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = new ArrayList<>();

  @Type(type = "jsonb")
  @Column(name = "extension_values", columnDefinition = "jsonb")
  private Map<String, Map<String, String>> extensionValues = Map.of();

  @Type(type = "jsonb")
  @Valid
  @Builder.Default
  private List<AgentRoles> contributors = List.of();

}
