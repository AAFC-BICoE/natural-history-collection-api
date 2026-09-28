package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;

import ca.gc.aafc.dina.entity.AgentRoles;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AgentRolesWithDate extends AgentRoles {

  private LocalDate date;

}
