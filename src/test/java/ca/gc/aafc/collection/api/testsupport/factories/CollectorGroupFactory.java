package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.UUID;

public class CollectorGroupFactory implements TestableEntityFactory<CollectorGroup> {

  @Override
  public CollectorGroup getEntityInstance() {
    return newCollectorGroup().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be further customized to return
   * the actual entity object, call the .build() method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static CollectorGroup.CollectorGroupBuilder newCollectorGroup() {
    return CollectorGroup
      .builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .uuid(UUID.randomUUID())
      .agentIdentifiers(new UUID[]{UUID.randomUUID()})
      .createdBy("test user");
  }

}
