package ca.gc.aafc.collection.api.entities;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
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

  @Column(name = "site_geom")
  private Geometry<G2D> siteGeom;

  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = List.of();

  @Type(PostgreSQLEnumType.class)
  @Enumerated(EnumType.STRING)
  private CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource;

  @Type(JsonType.class)
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
