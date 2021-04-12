package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.OffsetDateTime;

import org.javers.core.metamodel.annotation.Value;

@Data
@Builder
@Value // This class is considered a "value" belonging to a CollectingEventDto.
public class GeographicPlaceNameSourceDetail {
  private String sourceID;
  private String sourceIdType;
  private URL sourceUrl;
  private OffsetDateTime recordedOn;
}
