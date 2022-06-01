package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.dao.CollectionHierarchicalDataDAO;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Launches the application.
 */
// CHECKSTYLE:OFF HideUtilityClassConstructor (Configuration class can not have
// invisible constructor, ignore the check style error for this case)
@SpringBootApplication
@MapperScan(basePackageClasses = { PostgresHierarchicalDataService.class, CollectionSequenceMapper.class, CollectionHierarchicalDataDAO.class})
public class CollectionModuleApiLauncher {
  public static void main(String[] args) {
    SpringApplication.run(CollectionModuleApiLauncher.class, args);
  }
}
