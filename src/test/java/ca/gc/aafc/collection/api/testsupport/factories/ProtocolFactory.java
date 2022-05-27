package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class ProtocolFactory implements TestableEntityFactory<Protocol>{
    @Override
    public Protocol getEntityInstance() {
      return newProtocol().build();
    }
  
      /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
      public static Protocol.ProtocolBuilder<?, ?> newProtocol() {
        return Protocol
          .builder()
          .uuid(UUID.randomUUID())
          .createdBy("test user");
      }
  
}  
