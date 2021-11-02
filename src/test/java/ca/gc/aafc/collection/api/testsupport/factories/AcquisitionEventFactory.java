package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.AcquisitionEvent;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class AcquisitionEventFactory implements TestableEntityFactory<AcquisitionEvent> {
  
  @Override
  public AcquisitionEvent getEntityInstance() {
    return newAcquisitionEvent().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static AcquisitionEvent.AcquisitionEventBuilder newAcquisitionEvent() {
    return AcquisitionEvent
        .builder()
        .createdBy("test user");
  }
}
