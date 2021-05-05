package ca.gc.aafc.collection.api.entities;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@Value // This class is considered a "value" belonging to a CollectingEventDto.
public class GeographicPlaceNameSourceDetail {

  private URL sourceUrl;

  // It is customGeographicPlace OR selectedGeographicPlace but not both
  private String customGeographicPlace;
  private SourceAdministrativeLevel selectedGeographicPlace;

  // all SourceAdministrativeLevel (ordered) between selectedGeographicPlace and stateProvince/country
  // excluding them.
  private List<SourceAdministrativeLevel> higherGeographicPlaces;

  private SourceAdministrativeLevel stateProvince;
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
    private String id;
    private String element; // on OSM it's the osm_type (N, W or R: Node, Way or Relation) https://wiki.openstreetmap.org/wiki/Elements
    private String placeType; // on OSM it's usually the place_type (https://wiki.openstreetmap.org/wiki/Key:place) with a fallback on class:type
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
