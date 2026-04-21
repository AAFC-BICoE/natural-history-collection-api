package ca.gc.aafc.collection.api.entities;

import org.hibernate.annotations.Type;

import ca.gc.aafc.dina.entity.AgentRoles;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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

  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = new ArrayList<>();

  @Type(JsonType.class)
  @Column(name = "extension_values", columnDefinition = "jsonb")
  private Map<String, Map<String, String>> extensionValues = Map.of();

  @Type(JsonType.class)
  @Valid
  @Builder.Default
  private List<AgentRoles> contributors = List.of();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_project_id")
  @ToString.Exclude
  private Project parentProject;

}
