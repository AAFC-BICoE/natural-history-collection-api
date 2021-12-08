package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.URL;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@TypeDef(
  name = "jsonb",
  typeClass = JsonBinaryType.class
)
public class Institution extends UserDescribedDinaEntity {

  @URL
  private String webpage;

  @Size(max = 500)
  private String address;

  @Size(max = 1000)
  private String remarks;

  @Type(type = "jsonb")
  @Valid
  private List<InstitutionIdentifier> identifiers = new ArrayList<>();
}
