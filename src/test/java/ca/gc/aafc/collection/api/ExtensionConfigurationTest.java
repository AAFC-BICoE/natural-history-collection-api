package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

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
    Extension mixsSoil = extensionConfiguration.getExtension().get("mixs_soil");

    assertNotNull(mixsSoil);
    assertEquals("MIxS Soil", mixsSoil.getName());
    assertEquals("mixs_soil_v5", mixsSoil.getKey());
    assertEquals("v5", mixsSoil.getVersion());
  }

  @Test
  void getMixSSediment() {
    Extension mixsSediment = extensionConfiguration.getExtension().get("mixs_sediment");

    assertNotNull(mixsSediment);
    assertEquals("MIxS Sediment", mixsSediment.getName());
    assertEquals("mixs_sediment_v5", mixsSediment.getKey());
    assertEquals("v5", mixsSediment.getVersion());
  }

}
