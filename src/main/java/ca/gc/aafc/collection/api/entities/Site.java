package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;
import org.hibernate.annotations.Type;

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
  private String code;

  private Polygon<G2D> siteGeom;

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = List.of();

}
