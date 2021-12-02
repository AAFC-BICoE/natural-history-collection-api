package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.validator.constraints.URL;
import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@Value // This class is considered a "value" belonging to a CollectingEventDto.
public class GeographicPlaceNameSourceDetail {

  @URL
  private String sourceUrl;

  // It is customGeographicPlace OR selectedGeographicPlace but not both
  private String customGeographicPlace;

  @Valid
  private SourceAdministrativeLevel selectedGeographicPlace;

  // all SourceAdministrativeLevel (ordered) between selectedGeographicPlace and stateProvince/country
  // excluding them.
  @Valid
  private List<SourceAdministrativeLevel> higherGeographicPlaces;

  @Valid
  private SourceAdministrativeLevel stateProvince;

  @NotNull
  @Valid
  private Country country;

  private OffsetDateTime recordedOn;

  /**
   * Represents the information from the source concept
   * For OSM : 5356213,R,Gatineau
   */
  @Data
  @Builder
  @Value
  public static class SourceAdministrativeLevel {
    @NotBlank
    private String id;
    @NotBlank
    private String element; // on OSM it's the osm_type (N, W or R: Node, Way or Relation) https://wiki.openstreetmap.org/wiki/Elements
    @NotBlank
    private String placeType; // on OSM it's usually the place_type (https://wiki.openstreetmap.org/wiki/Key:place) with a fallback on class:type
    @NotBlank
    private String name;
  }

  @Data
  @Builder
  @Value
  public static class Country {
    // ISO code for the country
    @Size(min = 2, max = 2)
    private String code;
    private String name;
  }

}
