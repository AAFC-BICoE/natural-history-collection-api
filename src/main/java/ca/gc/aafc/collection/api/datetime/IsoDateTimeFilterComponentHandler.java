package ca.gc.aafc.collection.api.datetime;

import com.querydsl.core.types.Ops;

import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.dina.filter.FilterComponent;
import ca.gc.aafc.dina.filter.FilterExpression;
import ca.gc.aafc.dina.filter.FilterGroup;

/**
 * Handles the creation of {@link FilterComponent} for ISO 8601 date with partial date support.
 * <p>
 * This handler converts ISO date-time filter expressions into compound filter groups
 * that consider both the date-time value and its precision level.
 */
public class IsoDateTimeFilterComponentHandler {

  /**
   * Creates a {@link FilterComponent} for exact equality comparison of ISO date-times.
   * <p>
   * The resulting filter matches records where both the date-time value and precision
   * exactly match the provided filter expression.
   *
   * @param filterExpression the filter expression containing the ISO date-time value
   * @param precisionAttribute the attribute name for the precision field
   * @return a filter group with AND conjunction for date-time and precision equality
   */
  public static FilterComponent equal(FilterExpression filterExpression, String precisionAttribute) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());
    return FilterGroup.builder()
      .conjunction(FilterGroup.Conjunction.AND)
      .component(new FilterExpression(filterExpression.attribute(), Ops.EQ, isoDateTime.getLocalDateTime().toString()))
      .component(new FilterExpression(precisionAttribute, Ops.EQ, Byte.toString(isoDateTime.getFormat().getPrecision())))
      .build();
  }

  /**
   * Creates a {@link FilterComponent} for less-than or greater-than comparison of ISO date-times.
   * <p>
   * The resulting filter matches records where the date-time satisfies the comparison operator
   * and the precision is greater than or equal to the provided precision level.
   *
   * @param filterExpression the filter expression containing the ISO date-time value and comparison operator
   * @param precisionAttribute the attribute name for the precision field
   * @return a filter group with AND conjunction for date-time comparison and minimum precision
   */
  public static FilterComponent lessOrGreater(FilterExpression filterExpression, String precisionAttribute) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());
    return FilterGroup.builder()
      .conjunction(FilterGroup.Conjunction.AND)
      .component(new FilterExpression(filterExpression.attribute(), filterExpression.operator(), isoDateTime.getLocalDateTime().toString()))
      .component(new FilterExpression(precisionAttribute, Ops.GOE, Byte.toString(isoDateTime.getFormat().getPrecision())))
      .build();
  }
}
