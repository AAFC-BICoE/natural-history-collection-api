package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.CustomView;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;
import java.util.UUID;

public class CustomViewFactory implements TestableEntityFactory<CustomView> {

  @Override
  public CustomView getEntityInstance() {
    return newCustomView().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static CustomView.CustomViewBuilder newCustomView() {
    return CustomView
        .builder()
        .uuid(UUID.randomUUID())
        .name(RandomStringUtils.randomAlphabetic(5))
        .group(RandomStringUtils.randomAlphabetic(5))
        .restrictToCreatedBy(false)
        .viewConfiguration(Map.of("key1", 3, "key2", true));
  }
}
