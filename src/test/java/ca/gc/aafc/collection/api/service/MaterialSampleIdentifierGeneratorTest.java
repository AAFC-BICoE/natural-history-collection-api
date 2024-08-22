package ca.gc.aafc.collection.api.service;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleIdentifierGeneratorTest extends CollectionModuleBaseIT {


  @Inject
  private MaterialSampleIdentifierGenerator msig;

  @Test
  public void generateNextIdentifier_typeBasedStrategy_expectedIdentifier() {
    MaterialSample parent = MaterialSampleFactory.newMaterialSample()
      .materialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM)
      .materialSampleName("ABC-01")
      .build();
    materialSampleService.create(parent);
    MaterialSample child1 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(parent)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .materialSampleName("ABC-01-a")
      .build();
    materialSampleService.create(child1);

    MaterialSample child2 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(child1)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .materialSampleName("ABC-01-b")
      .build();
    materialSampleService.create(child2);

    MaterialSample child3 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(child1)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .materialSampleName("ABC-01-c")
      .build();
    materialSampleService.create(child3);

    // should not affect CULTURE_STRAIN series
    MaterialSample child4Molecular = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(child1)
      .materialSampleType(MaterialSample.MaterialSampleType.MOLECULAR_SAMPLE)
      .materialSampleName("ABC-01-c-A")
      .build();
    materialSampleService.create(child4Molecular);

    String nextIdentifier = msig.generateNextIdentifier(parent.getUuid(),
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED,
      MaterialSample.MaterialSampleType.CULTURE_STRAIN,
      MaterialSampleNameGeneration.CharacterType.LOWER_LETTER, null);
    assertEquals("ABC-01-d", nextIdentifier);

    String nextIdentifierMolecular = msig.generateNextIdentifier(child2.getUuid(),
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED,
      MaterialSample.MaterialSampleType.MOLECULAR_SAMPLE,
      MaterialSampleNameGeneration.CharacterType.LOWER_LETTER, null);
    assertEquals("ABC-01-b-B", nextIdentifierMolecular);
  }

  @Test
  public void generateNextIdentifier_directParentStrategy_expectedIdentifier() {
    MaterialSample parent = MaterialSampleFactory.newMaterialSample()
      .materialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM)
      .materialSampleName("ABC-01")
      .build();
    materialSampleService.create(parent);
    MaterialSample child1 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(parent)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .materialSampleName("ABC-01-a")
      .build();
    materialSampleService.create(child1);

    MaterialSample child2 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(child1)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .materialSampleName("ABC-01-a-A")
      .build();
    materialSampleService.create(child2);

    String nextIdentifier = msig.generateNextIdentifier(child2.getUuid(),
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT,
      null, MaterialSampleNameGeneration.CharacterType.LOWER_LETTER, null);
    assertEquals("ABC-01-a-A-a", nextIdentifier);

    nextIdentifier = msig.generateNextIdentifier(child1.getUuid(),
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT,
      null, MaterialSampleNameGeneration.CharacterType.UPPER_LETTER, null);
    assertEquals("ABC-01-a-B", nextIdentifier);

  }


  @Test
  public void testGetNext() {

    assertEquals("ABC-A3", msig.generateNextIdentifier("ABC-A2"));
    assertEquals("ABC-A03", msig.generateNextIdentifier("ABC-A02"));
    assertEquals("ABC-A10", msig.generateNextIdentifier("ABC-A09"));
    assertEquals("ABC-95-10", msig.generateNextIdentifier("ABC-95-9"));

    assertEquals("ABC-B", msig.generateNextIdentifier("ABC-A"));
    assertEquals("ABC-A03AA", msig.generateNextIdentifier("ABC-A03Z"));

    // lowercase input should return lowercase output
    assertEquals("ABC-A03ja", msig.generateNextIdentifier("ABC-A03iz"));

    // if there is a mix, uppercase will be preserved
    assertEquals("ABC-A-BA", msig.generateNextIdentifier("ABC-A-aZ"));
  }
}
