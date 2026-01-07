package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.validation.ControlledVocabularyItemValidator;

@Service
public class CollectionControlledVocabularyItemService extends ControlledVocabularyItemService<CollectionControlledVocabularyItem> {

  public CollectionControlledVocabularyItemService(BaseDAO baseDAO,
                                                   SmartValidator smartValidator,
                                                   ControlledVocabularyItemValidator itemValidator) {
    super(baseDAO, smartValidator, CollectionControlledVocabularyItem.class, itemValidator);
  }
}
