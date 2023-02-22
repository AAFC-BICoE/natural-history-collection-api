package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.TypedQuery;

@Service
public class MaterialSampleCounterService extends DefaultDinaService<MaterialSampleCounter> {

  private final BaseDAO baseDAO;

  public MaterialSampleCounterService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
    this.baseDAO = baseDAO;
  }

  public MaterialSampleCounter.IncrementFunctionOutput incrementCounter(int id, int amount) {
    TypedQuery<MaterialSampleCounter.IncrementFunctionOutput> tq =
            baseDAO.createWithEntityManager( em-> em.createNamedQuery(MaterialSampleCounter.INCREMENT_NAMED_QUERY,
                    MaterialSampleCounter.IncrementFunctionOutput.class));
    tq.setParameter("id", id);
    tq.setParameter("amount", amount);

    return tq.getSingleResult();

  }

}

