package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Value // This class is considered a "value" belonging to a Material Sample:
public class Organism {
  
  @NotNull
  private final UUID uuid;

  @Type(type = "jsonb")
  @Valid
  private final List<Determination> determination;

  @Size(max = 50)
  private final String lifeStage;

  @Size(max = 25)
  private final String sex;

  @Size(max = 50)
  private final String substrate;

  @Size(max = 1000)
  private final String remarks;

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
