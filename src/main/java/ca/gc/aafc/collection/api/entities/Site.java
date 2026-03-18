package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Site extends UserDescribedDinaEntity {

  public enum GeographicPlaceNameSource {
    OSM
  }

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @Size(max = 50)
  private String code;

  @Column(name = "site_geom")
  private Geometry<G2D> siteGeom;

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = List.of();

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private GeographicPlaceNameSource geographicPlaceNameSource;

  @Type(type = "jsonb")
  @Column(name = "geographic_place_name_source_details", columnDefinition = "jsonb")
  @Valid
  private GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;

  @Size(max = 100)
  private String dwcCountry;

  @Size(max = 2)
  private String dwcCountryCode;

  @Size(max = 100)
  private String dwcStateProvince;
}
