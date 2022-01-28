package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Value // This class is considered a "value" belonging to a Material Sample:
public class Organism {

  @NotNull
  private UUID uuid;

  @Valid
  private List<Determination> determination;

  @Size(max = 50)
  private String lifeStage;

  @Size(max = 25)
  private String sex;

  @Size(max = 50)
  private String substrate;

  @Size(max = 1000)
  private String remarks;

  /**
   * Count the number of primary Determination.
   * If there is no Determination 0 will be returned.
   *
   * @return the number of primary determination or 0 if there is no determinations
   */
  public long countPrimaryDetermination() {
    if (CollectionUtils.isEmpty(determination)) {
      return 0;
    }
    return determination.stream().filter(d -> d.getIsPrimary() != null && d.getIsPrimary()).count();
  }

}
