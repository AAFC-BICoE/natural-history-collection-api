package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.service.AcquisitionEventService;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.collection.api.service.CollectionSequenceService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.collection.api.service.CustomViewService;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.MaterialSampleActionDefinitionService;
import ca.gc.aafc.collection.api.service.MaterialSampleActionRunService;
import ca.gc.aafc.collection.api.service.OrganismService;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.service.ProjectService;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.keycloak.OAuth2Constants;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Properties;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
public class CollectionModuleBaseIT {
  @Inject
  protected DatabaseSupportService service;    

  @Inject
  protected CollectingEventService collectingEventService;

  @Inject
  protected CollectionManagedAttributeService collectionManagedAttributeService;

  @Inject
  protected MaterialSampleService materialSampleService;

  @Inject
  protected CustomViewService customViewService;

  @Inject
  protected MaterialSampleActionDefinitionService materialSampleActionDefinitionService;

  @Inject
  protected MaterialSampleActionRunService materialSampleActionRunService;

  @Inject
  protected PreparationTypeService preparationTypeService;

  @Inject
  protected CollectionService collectionService;

  @Inject
  protected CollectionSequenceService collectionSequenceService;

  @Inject
  protected StorageUnitService storageUnitService;

  @Inject
  protected StorageUnitTypeService storageUnitTypeService;

  @Inject
  protected AcquisitionEventService acquisitionEventService;

  @Inject
  protected ProjectService projectService;

  @Inject
  protected OrganismService organismService;

  @TestConfiguration
  private class CollectionModuleTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "collection-module-version");
      return new BuildProperties(props);
    }
  }
}

