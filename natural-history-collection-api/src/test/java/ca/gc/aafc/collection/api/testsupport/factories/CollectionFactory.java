package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import org.apache.commons.lang3.RandomStringUtils;

public class CollectionFactory implements TestableEntityFactory<Collection> {

    @Override
    public Collection getEntityInstance() {
      return newCollection().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static Collection.CollectionBuilder<?, ?> newCollection() {
      return Collection
        .builder()
        .name(RandomStringUtils.randomAlphabetic(5))
        .group(RandomStringUtils.randomAlphabetic(5))
        .code(RandomStringUtils.randomAlphabetic(5))
        .multilingualDescription(CollectionMethodTestFixture.newMulti())
        .createdBy(RandomStringUtils.randomAlphabetic(5));
    }

  }
