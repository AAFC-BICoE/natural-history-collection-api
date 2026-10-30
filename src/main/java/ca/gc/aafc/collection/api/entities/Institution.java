package ca.gc.aafc.collection.api.entities;

import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Institution extends UserDescribedDinaEntity {

  @URL
  private String webpage;

  @Size(max = 500)
  private String address;

  @Size(max = 1000)
  private String remarks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Valid
  private List<InstitutionIdentifier> identifiers = new ArrayList<>();
}
