package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = new ArrayList<>();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "extension_values", columnDefinition = "jsonb")
  private Map<String, Map<String, String>> extensionValues = Map.of();

  @JdbcTypeCode(SqlTypes.JSON)
  @Valid
  @Builder.Default
  private List<AgentRoles> contributors = List.of();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_project_id")
  @ToString.Exclude
  private Project parentProject;

}
