package ca.gc.aafc.collection.api.repository; 
import ca.gc.aafc.collection.api.dto.AgentRoleDto;
import ca.gc.aafc.collection.api.entities.AgentRole;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

@Repository
public class AgentRoleRepository extends DinaRepository<AgentRoleDto, AgentRole> {

  public AgentRoleRepository(
    @NonNull BaseDAO baseDAO,
    @NonNull DinaFilterResolver filterResolver,
    @NonNull BuildProperties props
  ) {
    super(
      new DefaultDinaService<>(baseDAO),
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(AgentRoleDto.class),
      AgentRoleDto.class,
      AgentRole.class,
      filterResolver,
      null,
      props);
  }
 }
