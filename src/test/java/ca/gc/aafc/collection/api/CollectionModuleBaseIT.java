package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.collection.api.service.CollectorGroupService;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;

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
  protected CollectorGroupService collectorGroupService;

  @Inject
  protected MaterialSampleService materialSampleService;

  @Inject
  protected PreparationProcessDefinitionService preparationProcessDefinitionService;

  @Inject
  protected PreparationProcessService preparationProcessService;

  @Inject
  protected PreparationTypeService preparationTypeService;
}

