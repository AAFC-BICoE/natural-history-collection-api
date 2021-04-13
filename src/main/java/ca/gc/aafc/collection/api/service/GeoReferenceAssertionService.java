package ca.gc.aafc.collection.api.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.validation.GeoreferenceAssertionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class GeoReferenceAssertionService extends DefaultDinaService<GeoreferenceAssertion> {

    private final GeoreferenceAssertionValidator georeferenceAssertionValidator;

    public GeoReferenceAssertionService(@NonNull BaseDAO baseDAO, @NonNull GeoreferenceAssertionValidator georeferenceAssertionValidator) {
        super(baseDAO);
        this.georeferenceAssertionValidator = georeferenceAssertionValidator;
    }

    @Override
    protected void preCreate(GeoreferenceAssertion entity) {    
      entity.setUuid(UUID.randomUUID());    
      validateGeoreferenceAssertion(entity);
    }

    @Override
    protected void preUpdate(GeoreferenceAssertion entity) {
      validateGeoreferenceAssertion(entity);
    }

    public void validateGeoreferenceAssertion(GeoreferenceAssertion entity) {
      Errors errors = new BeanPropertyBindingResult(entity, entity.getUuid().toString());
      georeferenceAssertionValidator.validate(entity, errors);
  
      if (!errors.hasErrors()) {
        return;
      }
  
      Optional<String> errorMsg = errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).findAny();
      errorMsg.ifPresent(msg -> {
        throw new IllegalArgumentException(msg);
      });
    }
    
}
