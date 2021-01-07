package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

@Repository
public class CollectingEventRepository extends DinaRepository<CollectingEventDto, CollectingEvent> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public CollectingEventRepository(
    @NonNull CollectingEventService dinaService,
    @NonNull BaseDAO baseDAO,
    @NonNull DinaFilterResolver filterResolver,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser
  ) {
    super(
      dinaService,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(CollectingEventDto.class),
      CollectingEventDto.class,
      CollectingEvent.class,
      filterResolver,
      null,
      props);  

    this.authenticatedUser = authenticatedUser;
 }

 @Override
 public <S extends CollectingEventDto> S create(S resource) {
   if (authenticatedUser.isPresent()) {
     resource.setCreatedBy(authenticatedUser.get().getUsername());
   }
   return super.create(resource);
 } 
}

