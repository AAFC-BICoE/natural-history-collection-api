package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

  @Size(max = 500)
  private String remarks;

  @Type(type = "jsonb")
  @Valid
  private List<CollectionIdentifier> identifiers = new ArrayList<>();

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_collection_id")
  @ToString.Exclude
  private Collection parentCollection;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_collection_id", referencedColumnName = "id", insertable = false, updatable = false)
  private List<Collection> subCollections = new ArrayList<>();

}
