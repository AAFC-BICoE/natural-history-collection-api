package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.i18n.MultilingualTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.UniqueElements;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "multilingual_title")
  @Valid
  private MultilingualTitle multilingualTitle;

  @JdbcTypeCode(SqlTypes.JSON)
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Column(name = "attachment", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> attachment = List.of();

}
