package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

  @Size(max = 500)
  private String remarks;

}
