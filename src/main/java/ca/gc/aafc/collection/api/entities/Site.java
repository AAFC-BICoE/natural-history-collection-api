package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import lombok.Builder;
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

  @Size(max = 50)
  @Column(name = "shorthand_code")
  private String shorthandCode;

  @Size(max = 50)
  @Column(name = "site_multimedia")
  private String siteMultimedia;

  private LocalDate startDate;
  private LocalDate endDate;

  @Type(type = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();
}
