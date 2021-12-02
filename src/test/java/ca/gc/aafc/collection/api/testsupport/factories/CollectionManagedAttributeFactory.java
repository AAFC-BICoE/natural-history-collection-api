package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class CollectionManagedAttributeFactory implements TestableEntityFactory<CollectionManagedAttribute> {

    @Override
    public CollectionManagedAttribute getEntityInstance() {
      return newCollectionManagedAttribute().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static CollectionManagedAttribute.CollectionManagedAttributeBuilder newCollectionManagedAttribute() {
      return CollectionManagedAttribute
          .builder()
          .uuid(UUID.randomUUID())
          .name(RandomStringUtils.randomAlphabetic(5))
          .group(RandomStringUtils.randomAlphabetic(5))
          .createdBy(RandomStringUtils.randomAlphabetic(5))
          .managedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING)
          .acceptedValues(new String[]{"value"})
          .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT)
          .multilingualDescription(CollectionMethodTestFixture.newMulti());
    }

  }
