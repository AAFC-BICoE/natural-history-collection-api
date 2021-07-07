package ca.gc.aafc.collection.api.entities;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Determination {

  public enum ScientificNameSource {
    ColPlus
  }

  public enum TypeStatus {
    TypeStatus
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private TypeStatus typeStatus;

  @Size(max = 250)
  private String typeStatusEvidence;

  private UUID determiner;

  private LocalDate determinedOn;

  @Size(max = 150)
  private String qualifier;

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private ScientificNameSource scientificNameSource;

  private String scientificNameDetails;

}
