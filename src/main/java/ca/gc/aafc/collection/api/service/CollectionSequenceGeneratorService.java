package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.function.BiFunction;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;
import org.springframework.web.server.MethodNotAllowedException;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.service.OnCreate;

import io.crnk.core.exception.ResourceNotFoundException;
import lombok.NonNull;

@Service
public class CollectionSequenceGeneratorService extends DefaultDinaService<CollectionSequenceGenerationRequest> {

  @Inject
  private CollectionService collectionService;

  @Inject
  private CollectionSequenceService collectionSequenceService;

  @Inject
  private CollectionSequenceMapper collectionSequenceMapper;

  public CollectionSequenceGeneratorService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator validator) {
    super(baseDAO, validator);
  }

  @Override
  public CollectionSequenceGenerationRequest create(CollectionSequenceGenerationRequest entity) {
    validateConstraints(entity, OnCreate.class);
    validateBusinessRules(entity);

    // Using the collection sequence mapper, retrieve the reserved ids. Both the
    // collection and collection sequence should exist since it was checked in the 
    // preCreate method.
    entity.setResult(collectionSequenceMapper.getNextId(
      collectionService.findOne(entity.getCollectionId(), Collection.class).getId(), 
      entity.getAmount())
    );

    return entity;
  }

  @Override
  public void validateBusinessRules(CollectionSequenceGenerationRequest entity) {
    // Check to ensure both the Collection and CollectionSequence exist.
    Collection collection = collectionService.findOne(entity.getCollectionId(), Collection.class);
    if (collection == null) {
      throw new ResourceNotFoundException(
          "Collection with the UUID of '" + entity.getCollectionId() + "' does not exist.");
    }

    CollectionSequence collectionSequence = collectionSequenceService.findOneById(collection.getId(),
        CollectionSequence.class);
    if (collectionSequence == null) {
      throw new ResourceNotFoundException(
          "There is no collection sequence associated with the collection with the UUID of '" 
              + entity.getCollectionId() + "'.");
    }
  }

  @Override
  public CollectionSequenceGenerationRequest update(CollectionSequenceGenerationRequest entity) {
    throw new MethodNotAllowedException("update", null);
  }

  @Override
  public void delete(CollectionSequenceGenerationRequest entity) {
    throw new MethodNotAllowedException("delete", null);
  }

  @Override
  public <T> T findOne(Object naturalId, Class<T> entityClass) {
    throw new MethodNotAllowedException("findOne", null);
  }

  @Override
  public <T> T getReferenceByNaturalId(Class<T> entityClass, Object naturalId) {
    throw new MethodNotAllowedException("getReferenceByNaturalId", null);
  }

  @Override
  public <T> List<T> findAll(@NonNull Class<T> entityClass,
      @NonNull BiFunction<CriteriaBuilder, Root<T>, Predicate[]> where,
      BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy, int startIndex, int maxResult) {
    throw new MethodNotAllowedException("findAll", null);
  }

  @Override
  public <T> List<T> findAll(@NonNull Class<T> entityClass, @NonNull PredicateSupplier<T> where,
      BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy, int startIndex, int maxResult) {
    throw new MethodNotAllowedException("findAll", null);
  }

  @Override
  public <T> Long getResourceCount(@NonNull Class<T> entityClass,
      @NonNull BiFunction<CriteriaBuilder, Root<T>, Predicate[]> predicateSupplier) {
    throw new MethodNotAllowedException("getResourceCount", null);
  }

  @Override
  public <T> Long getResourceCount(@NonNull Class<T> entityClass, @NonNull PredicateSupplier<T> predicateSupplier) {
    throw new MethodNotAllowedException("getResourceCount", null);
  }

  @Override
  public boolean exists(Class<?> entityClass, Object naturalId) {
    throw new MethodNotAllowedException("exists", null);
  }
}
