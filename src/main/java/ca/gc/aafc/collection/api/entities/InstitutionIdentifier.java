package ca.gc.aafc.collection.api.entities;

import java.io.Serializable;
import java.net.URI;

import org.javers.core.metamodel.annotation.Value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@Value
public class InstitutionIdentifier implements Serializable {
  
  public enum IdentifierType {
    GRSCICOLL
  }

  private IdentifierType type;
  private URI uri;
}
