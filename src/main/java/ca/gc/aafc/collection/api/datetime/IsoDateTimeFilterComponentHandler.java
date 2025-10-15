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
   * Creates a filter for "greater than" comparisons with partial ISO dates.
   * <p>
   * A date is considered greater only if it falls completely after the ambiguous range.
   * For example, "2004-06-15" > "2004" is false because 2004-06-15 is within 2004.
   * </p>
   *
   * @param filterExpression the filter expression containing the date value
   * @return a FilterExpression for the greater-than comparison
   */
  public static FilterComponent greater(FilterExpression filterExpression) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());

    // Simply check if stored date starts after the query range ends
    return new FilterExpression(
      filterExpression.attribute(),
      Ops.GT,
      isoDateTime.getLocalEndDateTime().toString()
    );
  }

  /**
   * Creates a filter component for "greater than or equal" comparisons with partial ISO dates.
   * <p>
   * A date is considered greater than or equal to a partial date if:
   * <ul>
   *   <li>It starts at or after the end of the query range (definitely greater), OR</li>
   *   <li>It represents the exact same range (equal - same start date AND same precision)</li>
   * </ul>
   * Uses strict logic where dates within but not equal to the range do NOT match.
   * </p>
   *
   * <h3>Examples:</h3>
   * <ul>
   *   <li>"2010" &gt;= "2009" = true (after the range)</li>
   *   <li>"2009" &gt;= "2009" = true (equal range: same start and precision)</li>
   *   <li>"2009-06-15" &gt;= "2009" = false (within but not equal)</li>
   *   <li>"2009-06" &gt;= "2009-06" = true (equal range)</li>
   *   <li>"2009-06-15" &gt;= "2009-06" = false (more specific, not equal)</li>
   *   <li>"2008" &gt;= "2009" = false (before the range)</li>
   * </ul>
   *
   * <h3>Implementation:</h3>
   * <p>
   * Generates a composite filter:
   * <pre>
   * (startEventDateTime >= queryEndDateTime)
   * OR
   * (startEventDateTime = queryStartDateTime AND precision = queryPrecision)
   * </pre>
   * This ensures both "greater than" and "equal" cases are properly handled.
   * </p>
   *
   * @param filterExpression the filter expression containing the date value to compare
   * @param precisionAttribute the attribute name that stores the precision information
   *                          (e.g., "startEventDateTimePrecision")
   * @return a FilterGroup representing the composite greater-than-or-equal filter
   */
  public static FilterComponent greaterOrEqual(
    FilterExpression filterExpression,
    String precisionAttribute) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());

    return FilterGroup.builder()
      .conjunction(FilterGroup.Conjunction.OR)
      .component(new FilterExpression(
        filterExpression.attribute(),
        Ops.GOE,
        isoDateTime.getLocalEndDateTime().toString()))
      .component(FilterGroup.builder()
        .conjunction(FilterGroup.Conjunction.AND)
        .component(new FilterExpression(
          filterExpression.attribute(),
          Ops.EQ,
          isoDateTime.getLocalDateTime().toString()))
        .component(new FilterExpression(
          precisionAttribute,
          Ops.EQ,
          Byte.toString(isoDateTime.getFormat().getPrecision())))
        .build())
      .build();
  }

  /**
   * Creates a filter component for "less than" comparisons with partial ISO dates.
   * <p>
   * A date is considered less than a partial date only if it falls completely before
   * the entire ambiguous range. Uses strict logic where dates within or equal to the
   * range are NOT considered to match.
   * </p>
   *
   * <h3>Examples:</h3>
   * <ul>
   *   <li>"2008" &lt; "2009" = true (before the range)</li>
   *   <li>"2009-06-15" &lt; "2009" = false (within the range)</li>
   *   <li>"2009" &lt; "2009" = false (equal range)</li>
   *   <li>"2010" &lt; "2009" = false (after the range)</li>
   *   <li>"2009-05-31" &lt; "2009-06" = true (before June 2009)</li>
   * </ul>
   *
   * <h3>Implementation:</h3>
   * <p>
   * Generates a filter: {@code startEventDateTime < queryStartDateTime}
   * where queryStartDateTime is the earliest possible moment of the partial date
   * (e.g., "2009" becomes "2009-01-01T00:00:00").
   * </p>
   *
   * @param filterExpression the filter expression containing the date value to compare
   * @return a FilterExpression for the less-than comparison
   * @see ISODateTime#parse(String)
   * @see ISODateTime#getLocalDateTime()
   */
  public static FilterComponent less(FilterExpression filterExpression) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());

    // Check if stored date ends before the query range starts
    return new FilterExpression(
      filterExpression.attribute(),
      Ops.LT,
      isoDateTime.getLocalDateTime().toString()
    );
  }

  /**
   * Creates a filter component for "less than or equal" comparisons with partial ISO dates.
   * <p>
   * A date is considered less than or equal to a partial date if:
   * <ul>
   *   <li>It ends at or before the start of the query range (definitely less), OR</li>
   *   <li>It represents the exact same range (equal - same start date AND same precision)</li>
   * </ul>
   * Uses strict logic where dates within but not equal to the range do NOT match.
   * </p>
   *
   * <h3>Examples:</h3>
   * <ul>
   *   <li>"2008" &lt;= "2009" = true (before the range)</li>
   *   <li>"2009" &lt;= "2009" = true (equal range: same start and precision)</li>
   *   <li>"2009-06-15" &lt;= "2009" = false (within but not equal)</li>
   *   <li>"2009-06" &lt;= "2009-06" = true (equal range)</li>
   *   <li>"2009-06-15" &lt;= "2009-06" = false (more specific, not equal)</li>
   *   <li>"2010" &lt;= "2009" = false (after the range)</li>
   * </ul>
   *
   * <h3>Implementation:</h3>
   * <p>
   * Generates a composite filter:
   * <pre>
   * (startEventDateTime <= queryStartDateTime)
   * OR
   * (startEventDateTime = queryStartDateTime AND precision = queryPrecision)
   * </pre>
   * This ensures both "less than" and "equal" cases are properly handled.
   * </p>
   *
   * @param filterExpression the filter expression containing the date value to compare
   * @param precisionAttribute the attribute name that stores the precision information
   *                          (e.g., "startEventDateTimePrecision")
   * @return a FilterGroup representing the composite less-than-or-equal filter
   */
  public static FilterComponent lessOrEqual(
    FilterExpression filterExpression,
    String precisionAttribute) {
    ISODateTime isoDateTime = ISODateTime.parse(filterExpression.value());

    return FilterGroup.builder()
      .conjunction(FilterGroup.Conjunction.OR)
      // Case 1: Stored date ENDS at or before query range ENDS (definitely <=)
      .component(new FilterExpression(
        filterExpression.attribute() + "End",  // Need stored END datetime
        Ops.LOE,
        isoDateTime.getLocalEndDateTime().toString()))
      // Case 2: Equal ranges - same start date AND same precision
      .component(FilterGroup.builder()
        .conjunction(FilterGroup.Conjunction.AND)
        .component(new FilterExpression(
          filterExpression.attribute(),
          Ops.EQ,
          isoDateTime.getLocalDateTime().toString()))
        .component(new FilterExpression(
          precisionAttribute,
          Ops.EQ,
          Byte.toString(isoDateTime.getFormat().getPrecision())))
        .build())
      .build();
  }
}
