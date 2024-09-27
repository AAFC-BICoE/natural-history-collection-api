package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.IdentifierTypeDto;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import javax.inject.Inject;

public class IdentifierTypeRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private IdentifierTypeRepository identifierTypeRepository;

  @Test
  public void findAll_identifierTypeRepository() {
    List<IdentifierTypeDto> identifierTypeDtos =
      identifierTypeRepository.findAll("");
    assertFalse(identifierTypeDtos.isEmpty());
  }
}
