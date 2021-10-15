package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociationDto {

  private UUID associatedSample;
  private String associationType;

  public static class AssociationListMapperAdapter
    implements DinaFieldAdapter<MaterialSampleDto, MaterialSample, List<AssociationDto>, List<Association>> {

    @Override
    public List<AssociationDto> toDTO(List<Association> associations) {
      if (CollectionUtils.isNotEmpty(associations)) {
        return associations.stream()
          .map(association -> AssociationDto.builder()
            .associatedSample(association.getAssociatedSample().getUuid())
            .associationType(association.getAssociationType())
            .build())
          .collect(Collectors.toList());
      }
      return Collections.emptyList();
    }

    @Override
    public List<Association> toEntity(List<AssociationDto> associationDtos) {
      if (CollectionUtils.isNotEmpty(associationDtos)) {
        return associationDtos.stream()
          .map(association -> Association.builder()
            .associatedSample(MaterialSample.builder().uuid(association.getAssociatedSample()).build())
            .associationType(association.getAssociationType())
            .build())
          .collect(Collectors.toList());
      }
      return Collections.emptyList();
    }

    @Override
    public Consumer<List<Association>> entityApplyMethod(MaterialSample entityRef) {
      return entityRef::setAssociations;
    }

    @Override
    public Consumer<List<AssociationDto>> dtoApplyMethod(MaterialSampleDto dtoRef) {
      return dtoRef::setAssociations;
    }

    @Override
    public Supplier<List<Association>> entitySupplyMethod(MaterialSample entityRef) {
      return () -> {
        List<Association> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityRef.getAssociatedBy())) {
          list.addAll(entityRef.getAssociatedBy().stream()
            .peek(association -> association.setAssociatedSample(association.getSample()))
            .collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(entityRef.getAssociations())) {
          list.addAll(entityRef.getAssociations());
        }
        return list;
      };
    }

    @Override
    public Supplier<List<AssociationDto>> dtoSupplyMethod(MaterialSampleDto dtoRef) {
      return dtoRef::getAssociations;
    }
  }
}
