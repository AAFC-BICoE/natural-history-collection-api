package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Protocol extends UserDescribedDinaEntity {

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @Type(type = "list-array")
  @Column(name = "attachments", columnDefinition = "uuid[]")
  private List<UUID> attachments = new ArrayList<>();
}