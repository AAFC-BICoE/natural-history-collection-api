package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class CollectingEventFactory implements TestableEntityFactory<CollectingEvent> {

    @Override
    public CollectingEvent getEntityInstance() {
      return newCollectingEvent().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static CollectingEvent.CollectingEventBuilder newCollectingEvent() {
      return CollectingEvent
          .builder()
          .group("test group")
          .createdBy("test user");
    }

  }
