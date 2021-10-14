package ca.gc.aafc.collection.api.entities;

import java.net.URI;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractIdentifier {

  private URI uri;
  
}
