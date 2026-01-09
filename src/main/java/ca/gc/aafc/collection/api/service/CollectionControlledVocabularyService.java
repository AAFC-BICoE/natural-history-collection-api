package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ControlledVocabularyService;

@Service
public class CollectionControlledVocabularyService extends ControlledVocabularyService<CollectionControlledVocabulary> {

  public CollectionControlledVocabularyService(BaseDAO baseDAO,
                                               SmartValidator smartValidator) {
    super(baseDAO, smartValidator, CollectionControlledVocabulary.class);
  }
}
