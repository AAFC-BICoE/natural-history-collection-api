package ca.gc.aafc.collection.api.entities;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.URL;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> identifiers = Map.of();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_collection_id")
  @ToString.Exclude
  private Collection parentCollection;

}
