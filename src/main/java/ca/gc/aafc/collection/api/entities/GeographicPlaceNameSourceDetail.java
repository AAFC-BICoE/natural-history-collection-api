package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.OffsetDateTime;

@Data
@Builder
public class GeographicPlaceNameSourceDetail {
  private String sourceID;
  private String sourceIdType;
  private URL sourceUrl;
  private OffsetDateTime recordedOn;
}
