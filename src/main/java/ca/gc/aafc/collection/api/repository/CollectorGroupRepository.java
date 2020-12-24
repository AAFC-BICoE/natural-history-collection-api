package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

@Repository
public class CollectorGroupRepository extends DinaRepository<CollectorGroupDto, CollectorGroup> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public CollectorGroupRepository(
    @NonNull BaseDAO baseDAO,
    @NonNull DinaFilterResolver filterResolver,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      new DefaultDinaService<>(baseDAO),
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(CollectorGroupDto.class),
      CollectorGroupDto.class,
      CollectorGroup.class,
      filterResolver,
      null,
      props);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectorGroupDto> S create(S resource) {
    if (dinaAuthenticatedUser.isPresent()) {
      resource.setCreatedBy(dinaAuthenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }

 }
