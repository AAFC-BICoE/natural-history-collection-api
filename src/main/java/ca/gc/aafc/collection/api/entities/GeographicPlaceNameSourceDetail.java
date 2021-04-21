package ca.gc.aafc.collection.api.entities;

import java.net.URL;
import java.time.OffsetDateTime;

import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Value // This class is considered a "value" belonging to a CollectingEventDto.
public class GeographicPlaceNameSourceDetail {
  private String sourceID;
  private String sourceIdType;
  private URL sourceUrl;
  private OffsetDateTime recordedOn;
  private StateProvince stateProvince;
  private Country country;

  @Data
  @Builder
  @Value
  public static class StateProvince {
    private String id;
    private String name;
  }

  @Data
  @Builder
  @Value
  public static class Country {
    private String id;
    private String name;
  }

}
