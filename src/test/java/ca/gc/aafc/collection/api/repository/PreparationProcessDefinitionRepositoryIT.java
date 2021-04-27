package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationProcessDefinitionDto;

import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloack.enabled=true")
public class PreparationProcessDefinitionRepositoryIT extends CollectionModuleBaseIT {
  
  // @Inject 
  // private PreparationProcessDefinitionRepository preparationProcessDefinitionRepository;
  
  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  private PreparationProcessDefinitionDto newPreparationProcessDefinitionDto() {
    PreparationProcessDefinitionDto ppd = new PreparationProcessDefinitionDto();
    ppd.setName(name);
    ppd.setGroup(group);
    return ppd;
  }
}
