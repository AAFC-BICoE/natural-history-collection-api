package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.MaterialSampleTypeService;
import ca.gc.aafc.collection.api.service.MaterialSampleActionDefinitionService;
import ca.gc.aafc.collection.api.service.MaterialSampleActionRunService;
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
  protected MaterialSampleService materialSampleService;

  @Inject
  protected MaterialSampleActionDefinitionService materialSampleActionDefinitionService;

  @Inject
  protected MaterialSampleActionRunService materialSampleActionRunService;

  @Inject
  protected PreparationTypeService preparationTypeService;

  @Inject
  protected MaterialSampleTypeService materialSampleTypeService;

  @Inject
  protected CollectionService collectionService;
}

