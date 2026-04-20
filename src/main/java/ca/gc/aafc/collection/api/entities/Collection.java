package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
import org.hibernate.validator.constraints.URL;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Collection extends UserDescribedDinaEntity {

  @NotBlank
  @Column(name = "_group")
  @Size(max = 250)
  private String group;

  @Size(max = 10)
  private String code;

  @ManyToOne
  private Institution institution;

  @URL
  private String webpage;

  @Size(max = 500)
  private String contact;
  
  @Size(max = 500)
  private String address;

  @Size(max = 1000)
  private String remarks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  @Valid
  private List<CollectionIdentifier> identifiers = new ArrayList<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_collection_id")
  @ToString.Exclude
  private Collection parentCollection;

}
