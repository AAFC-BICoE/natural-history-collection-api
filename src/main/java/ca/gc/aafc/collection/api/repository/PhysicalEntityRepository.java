package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.PhysicalEntityDto;
import ca.gc.aafc.collection.api.entities.PhysicalEntity;
import ca.gc.aafc.collection.api.service.PhysicalEntityService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

@Repository
public class PhysicalEntityRepository extends DinaRepository<PhysicalEntityDto, PhysicalEntity> {
    
    private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

    public PhysicalEntityRepository(
        @NonNull PhysicalEntityService dinaService,
        ExternalResourceProvider externalResourceProvider,
        @NonNull BuildProperties buildProperties,
        Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
    ) {
        super(
            dinaService,
            Optional.empty(),
            Optional.empty(),
            new DinaMapper<>(PhysicalEntityDto.class),
            PhysicalEntityDto.class,
            PhysicalEntity.class,
            null,
            externalResourceProvider,
            buildProperties);
        this.dinaAuthenticatedUser = dinaAuthenticatedUser;
    }

    @Override
    public <S extends PhysicalEntityDto> S create(S resource) {
      dinaAuthenticatedUser.ifPresent(
        authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
      return super.create(resource);
    }
}
