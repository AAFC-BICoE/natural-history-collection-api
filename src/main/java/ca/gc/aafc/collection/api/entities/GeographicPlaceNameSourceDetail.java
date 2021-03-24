package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeographicPlaceNameSourceDetail {
  private String sourceID;
  private String sourceUrl;
  private String date;
}
