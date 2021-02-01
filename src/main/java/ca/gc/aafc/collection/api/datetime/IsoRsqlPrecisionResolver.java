package ca.gc.aafc.collection.api.datetime;

import ca.gc.aafc.collection.api.datetime.ISODateTime.Format;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<Node, Set<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final Map<String, String> fieldToPrecisionNameMap;

  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldToPrecisionNameMap.keySet()).toString();
  }

  @Override
  public Node visit(AndNode andNode, Set<String> field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public Node visit(OrNode orNode, Set<String> field) {
    return resolveLogicalNode(orNode, field);
  }

  private Node resolveLogicalNode(LogicalNode logicalNode, Set<String> field) {
    return logicalNode.withChildren(logicalNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(ComparisonNode node, Set<String> field) {
    if (isSelectedField(field, node.getSelector())) {
      if (node.getOperator().equals(RSQLOperators.EQUAL)) {
        ISODateTime argument = node.getArguments().stream().findFirst().map(ISODateTime::parse).orElseThrow();
        LocalDateTime lowerBound = argument.getLocalDateTime();
        LocalDateTime upperBound = getUpperBoundForFormat(lowerBound, argument.getFormat());
        return newRangeNode(lowerBound, upperBound, node.getSelector());
      } else {
        return node;
      }
    } else {
      return node;
    }
  }

  private static AndNode newRangeNode(LocalDateTime lower, LocalDateTime upper, String selector) {
    return new AndNode(List.of(
      new ComparisonNode(RSQLOperators.GREATER_THAN_OR_EQUAL, selector, List.of(lower.toString())),
      new ComparisonNode(RSQLOperators.LESS_THAN_OR_EQUAL, selector, List.of(upper.toString())))
    );
  }

  private static LocalDateTime getUpperBoundForFormat(LocalDateTime lowerBound, Format format) {
    LocalDateTime upperBound = null;
    switch (format) {
      case YYYY:
        upperBound = lowerBound.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
          .plusYears(1).minusNanos(1);
        break;
      case YYYY_MM:
        upperBound = lowerBound.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
          .plusMonths(1).minusNanos(1);
        break;
      case YYYY_MM_DD:
        upperBound = lowerBound.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM:
        upperBound = lowerBound.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM_SS:
        upperBound = lowerBound.withNano(0).plusSeconds(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM_SS_MMM:
        upperBound = lowerBound;
        break;
    }
    return upperBound;
  }

  private static boolean isSelectedField(Set<String> field, String selector) {
    return field.stream().anyMatch(selector::equalsIgnoreCase);
  }

}
