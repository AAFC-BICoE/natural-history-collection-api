package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to resolve Iso Date time partial dates.
 */
@AllArgsConstructor
public class IsoDateTimeRsqlResolver implements RSQLVisitor<Node, String> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final String fieldName;
  private final String precisionName;

  /**
   * Resolves the given rsql string for dates tracked by the resolver. Blank strings are returned.
   *
   * @param rsql - string to resolve
   * @return - Resolved rsql
   */
  public String resolveDates(String rsql) {
    if (StringUtils.isBlank(rsql)) {
      return rsql;
    }
    return RSQL_PARSER.parse(rsql).accept(this, fieldName).toString();
  }

  @Override
  public Node visit(AndNode andNode, String field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public Node visit(OrNode orNode, String field) {
    return resolveLogicalNode(orNode, field);
  }

  private Node resolveLogicalNode(LogicalNode logicalNode, String field) {
    return logicalNode.withChildren(logicalNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(ComparisonNode node, String field) {
    String selector = node.getSelector();
    if (isSelectedField(field, selector) && node.getOperator().equals(RSQLOperators.EQUAL)) {
      ISODateTime argument = node.getArguments().stream().findFirst().map(ISODateTime::parse).orElseThrow();
      List<String> precision = List.of(Byte.toString(argument.getFormat().getPrecision()));
      return new AndNode(List.of(
        new ComparisonNode(RSQLOperators.EQUAL, selector, List.of(argument.getLocalDateTime().toString())),
        new ComparisonNode(RSQLOperators.EQUAL, precisionName, precision)
      ));
    } else {
      return node;
    }
  }

  private static boolean isSelectedField(String field, String selector) {
    return field.equalsIgnoreCase(selector);
  }

}
