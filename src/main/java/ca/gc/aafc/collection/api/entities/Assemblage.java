package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Assemblage extends UserDescribedDinaEntity {

  @NotBlank
  @Column(name = "_group")
  @Size(max = 250)
  private String group;

  @Type(type = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> attachment = List.of();

}
