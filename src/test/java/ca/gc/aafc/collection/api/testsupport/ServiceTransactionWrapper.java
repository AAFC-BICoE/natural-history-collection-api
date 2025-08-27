package ca.gc.aafc.collection.api.testsupport;

import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceTransactionWrapper {

  @Transactional
  public <E> E execute (Function<E, E> dinaServiceMethod, E entity) {
    return dinaServiceMethod.apply(entity);
  }

}
