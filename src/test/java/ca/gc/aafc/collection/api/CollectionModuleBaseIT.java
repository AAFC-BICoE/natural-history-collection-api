package ca.gc.aafc.collection.api;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import ca.gc.aafc.collection.api.service.AssemblageService;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.collection.api.service.CollectionSequenceService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.collection.api.service.ExpeditionService;
import ca.gc.aafc.collection.api.service.FormTemplateService;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.OrganismService;
import ca.gc.aafc.collection.api.service.PreparationMethodService;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.service.ProjectService;
import ca.gc.aafc.collection.api.service.ProtocolService;
import ca.gc.aafc.collection.api.service.SiteService;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
import ca.gc.aafc.collection.api.service.StorageUnitUsageService;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;

import java.util.Properties;
import javax.inject.Inject;
import javax.transaction.Transactional;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Import(CollectionModuleBaseIT.CollectionModuleTestConfiguration.class)
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
  protected FormTemplateService formTemplateService;

  @Inject
  protected PreparationTypeService preparationTypeService;

  @Inject
  protected PreparationMethodService preparationMethodService;

  @Inject
  protected ProtocolService protocolService;

  @Inject
  protected CollectionService collectionService;

  @Inject
  protected CollectionSequenceService collectionSequenceService;

  @Inject
  protected StorageUnitService storageUnitService;

  @Inject
  protected StorageUnitUsageService storageUnitUsageService;

  @Inject
  protected StorageUnitTypeService storageUnitTypeService;

  @Inject
  protected ProjectService projectService;

  @Inject
  protected AssemblageService assemblageService;

  @Inject
  protected OrganismService organismService;

  @Inject
  protected ExpeditionService expeditionService;

  @Inject
  protected SiteService siteService;

  @TestConfiguration
  public static class CollectionModuleTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "collection-module-version");
      return new BuildProperties(props);
    }
  }
}
