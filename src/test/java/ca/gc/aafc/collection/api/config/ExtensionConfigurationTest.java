package ca.gc.aafc.collection.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ExtensionConfigurationTest extends CollectionModuleBaseIT {
  
  @Inject
  private CollectionExtensionConfiguration extensionConfiguration;

  @Test
  void getMixSSoil() {
    Extension mixsSoil = extensionConfiguration.getExtension().get("mixs_soil_v5");

    assertNotNull(mixsSoil);
    assertEquals("MIxS Soil v5", mixsSoil.getName());
    assertEquals("mixs_soil_v5", mixsSoil.getKey());
    assertEquals("v5", mixsSoil.getVersion());
  }

  @Test
  void getMixSSediment() {
    Extension mixsSediment = extensionConfiguration.getExtension().get("mixs_sediment_v4");

    assertNotNull(mixsSediment);
    assertEquals("MIxS Sediment v4", mixsSediment.getName());
    assertEquals("mixs_sediment_v4", mixsSediment.getKey());
    assertEquals("v4", mixsSediment.getVersion());
  }

  @Test
  void getCfiaPpc() {
    Extension cfiaPpc = extensionConfiguration.getExtension().get("cfia_ppc");

    assertNotNull(cfiaPpc);
    assertEquals("CFIA Plant pest containment", cfiaPpc.getName());
    assertEquals("cfia_ppc", cfiaPpc.getKey());
    assertEquals("2022-02", cfiaPpc.getVersion());
  }

  @Test
  void getAgronomyOntology() {
    Extension agronomyOntology = extensionConfiguration.getExtension().get("agronomy_ontology_v1");

    assertNotNull(agronomyOntology);
    assertEquals("Agronomy Ontology", agronomyOntology.getName());
    assertEquals("agronomy_ontology_v1", agronomyOntology.getKey());
    assertEquals("v1", agronomyOntology.getVersion());
  }
}
