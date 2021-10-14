package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class CollectionIdentifier extends AbstractIdentifier {

  public enum IdentifierType {
    GRSciColl, Index_Herbariorum
  }

  private IdentifierType type ;
  
}
