package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.ArrayList;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.util.UUIDHelper;

import org.apache.commons.lang3.RandomStringUtils;

public class MaterialSampleFactory implements TestableEntityFactory<MaterialSample> {

    @Override
    public MaterialSample getEntityInstance() {
        return newMaterialSample().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static MaterialSample.MaterialSampleBuilder<?, ?> newMaterialSample() {
        return MaterialSample
            .builder()
            .barcode(RandomStringUtils.randomAlphabetic(5))
            .uuid(UUIDHelper.generateUUIDv7())
            .materialSampleName("soil sample")
            .associations(new ArrayList<>())
            .createdBy("test user")
            .group("aafc")
            .allowDuplicateName(true);
    }

    public static MaterialSample.MaterialSampleBuilder<?, ?> newMaterialSampleNoRelationships() {
        return newMaterialSample()
            ;
    }
    
}
