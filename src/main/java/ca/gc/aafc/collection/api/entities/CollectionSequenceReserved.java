package ca.gc.aafc.collection.api.entities;

import lombok.Data;

@Data
public class CollectionSequenceReserved {
  /**
   * Lowest reserved ID in the range.
   */
  private int lowReservedID;

  /**
   * Highest reserved ID in the range.
   */
  private int highReservedID;  
}
