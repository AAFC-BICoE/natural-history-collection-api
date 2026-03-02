package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

/**
 * Static method that can be called to return a configured builder that can be
 * further customized to return the actual entity object, call the .build()
 * method on a builder.
 *
 * @return Pre-configured builder with all mandatory fields set
 */
public class SiteFactory implements TestableEntityFactory<Site> {
  public static Site.SiteBuilder<?, ?> newSite() {
    return Site.builder()
        .group("aafc")
        .name(TestableEntityFactory.generateRandomNameLettersOnly(7))
        .uuid(UUID.randomUUID())
        .createdBy("test user");
  }

  @Override
  public Site getEntityInstance() {
    return newSite().build();
  }
}
