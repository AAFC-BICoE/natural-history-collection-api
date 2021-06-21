package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleTypeFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.entity.ManagedAttribute.ManagedAttributeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import javax.validation.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")

public class MaterialSampleCRUDIT extends CollectionModuleBaseIT {

    @Inject
    private PreparationTypeService preparationTypeService;

    @Inject
    private MaterialSampleService materialSampleService;

    private List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

    private static final String dwcCatalogNumber = "S-4313";
    private static final String[] dwcOtherCatalogNumbers = new String[]{"A-1111", "B-2222"};
    private static final String expectedCreatedBy = "dina-save";
    private static final String sampleMaterialName = "lake water sample";
    private static final UUID preparedBy = UUID.randomUUID();
    private static final String preparationExpectedCreatedBy = "preparation-dina-save";
    private static final String preparationExpectedGroup = "dina-group-save";
    private static final String preparationExpectedName = "isolate lake water sample";
    private static final String materialSampleTypeExpectedCreatedBy = "material-sample-type-save";
    private static final String materialSampleTypeExpectedGroup = "material-sample-type-group-save";
    private static final String materialSampleTypeExpectedName = "liquid lake water sample";
    private static final LocalDate preparationDate = LocalDate.now();
    private PreparationType preparationType;
    private MaterialSampleType materialSampleType;
    private MaterialSample materialSample;

    @BeforeEach
    void setup() {
        preparationType = PreparationTypeFactory.newPreparationType()
            .createdBy(preparationExpectedCreatedBy)
            .group(preparationExpectedGroup)
            .name(preparationExpectedName)
            .build();
        preparationTypeService.create(preparationType);

        materialSampleType = MaterialSampleTypeFactory.newMaterialSampleType()
            .createdBy(materialSampleTypeExpectedCreatedBy)
            .group(materialSampleTypeExpectedGroup)
            .name(materialSampleTypeExpectedName)
            .build();
        materialSampleTypeService.create(materialSampleType);

        materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .dwcOtherCatalogNumbers(dwcOtherCatalogNumbers)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .preparedBy(preparedBy)
            .materialSampleName(sampleMaterialName)
            .preparationDate(preparationDate)
            .preparationType(preparationType)
            .materialSampleType(materialSampleType)
            .build();
        materialSampleService.create(materialSample);
    }

    @Test
    public void testCreate() {
        
        assertNotNull(preparationType.getId());
        assertNotNull(materialSample.getId());

        assertEquals(dwcCatalogNumber, materialSample.getDwcCatalogNumber());
        assertEquals(dwcOtherCatalogNumbers, materialSample.getDwcOtherCatalogNumbers());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());

        assertEquals(preparationType.getId(), materialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, materialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, materialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, materialSample.getPreparationType().getName());

        assertEquals(materialSampleType.getId(), materialSample.getMaterialSampleType().getId());
        assertEquals(materialSampleTypeExpectedCreatedBy, materialSample.getMaterialSampleType().getCreatedBy());
        assertEquals(materialSampleTypeExpectedGroup, materialSample.getMaterialSampleType().getGroup());
        assertEquals(materialSampleTypeExpectedName, materialSample.getMaterialSampleType().getName());

        assertEquals(preparedBy, materialSample.getPreparedBy());
        assertEquals(preparationDate, materialSample.getPreparationDate());
    }

    @Test
    public void testFind() {

        MaterialSample fetchedMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);

        assertEquals(materialSample.getId(), fetchedMaterialSample.getId());
        assertEquals(dwcCatalogNumber, fetchedMaterialSample.getDwcCatalogNumber());
        assertEquals(dwcOtherCatalogNumbers, fetchedMaterialSample.getDwcOtherCatalogNumbers());
        assertEquals(expectedCreatedBy, fetchedMaterialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, fetchedMaterialSample.getAttachment());
        assertEquals(sampleMaterialName, fetchedMaterialSample.getMaterialSampleName());

        assertEquals(preparationType.getId(), fetchedMaterialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, fetchedMaterialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, fetchedMaterialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, fetchedMaterialSample.getPreparationType().getName());

        assertEquals(materialSampleType.getId(), materialSample.getMaterialSampleType().getId());
        assertEquals(materialSampleTypeExpectedCreatedBy, materialSample.getMaterialSampleType().getCreatedBy());
        assertEquals(materialSampleTypeExpectedGroup, materialSample.getMaterialSampleType().getGroup());
        assertEquals(materialSampleTypeExpectedName, materialSample.getMaterialSampleType().getName());
        
        assertEquals(preparedBy, fetchedMaterialSample.getPreparedBy());
        assertEquals(preparationDate, fetchedMaterialSample.getPreparationDate());
    }

    @Test
    public void testParentChildRelationship() {
        MaterialSample parent = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber("parent-" + dwcCatalogNumber)
            .createdBy("parent-" + expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName("parent-" + sampleMaterialName)
            .preparationType(preparationType)
            .build();

        MaterialSample child = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber("child-" + dwcCatalogNumber)
            .createdBy("child-" + expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName("child-" + sampleMaterialName)
            .preparationType(preparationType)
            .parentMaterialSample(parent)
            .build();

        parent.setMaterialSampleChildren(Collections.singletonList(child));
        
        materialSampleService.create(parent);
        materialSampleService.create(child);

        MaterialSample fetchedParent = materialSampleService.findOne(parent.getUuid(), MaterialSample.class);

        assertEquals(fetchedParent.getUuid(), child.getParentMaterialSample().getUuid());
        assertEquals(1, fetchedParent.getMaterialSampleChildren().size());
        assertEquals(child.getUuid(), fetchedParent.getMaterialSampleChildren().get(0).getUuid());
    }

    @Test
    void validate_WhenValidStringType() {
      CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{})
        .build();
  
      collectionManagedAttributeService.create(testManagedAttribute);
 
      materialSample.setManagedAttributeValues(Map.of(testManagedAttribute.getKey(), "anything"));
      materialSampleService.update(materialSample);
    }
  
    @Test
    void validate_WhenInvalidIntegerTypeExceptionThrown() {
      CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{})
        .managedAttributeType(ManagedAttributeType.INTEGER)
        .build();
  
      collectionManagedAttributeService.create(testManagedAttribute);
  
      materialSample.setManagedAttributeValues(Map.of(testManagedAttribute.getKey(), "1.2"));

      assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
    }
  
    @Test
    void assignedValueContainedInAcceptedValues_validationPasses() {
      CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .build();
  
      collectionManagedAttributeService.create(testManagedAttribute);
  
      materialSample.setManagedAttributeValues(Map.of(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]));
      materialSampleService.update(materialSample);
    }
  
    @Test
    void assignedValueNotContainedInAcceptedValues_validationPasses() {
      CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
        .acceptedValues(new String[]{"val1", "val2"})
        .build();
  
      collectionManagedAttributeService.create(testManagedAttribute);
  
      materialSample.setManagedAttributeValues(Map.of(testManagedAttribute.getKey(), "val3"));
      assertThrows(ValidationException.class, () ->  materialSampleService.update(materialSample));
    }
    
}
