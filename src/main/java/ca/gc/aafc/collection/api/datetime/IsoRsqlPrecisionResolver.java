package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Rsql visitor to support rsql date filtering with partial dates. Partial dates are resolved using {@link
 * ISODateTime#parse(String)}
 */
@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<String, List<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final List<String> fieldList;

  public String parse(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldList);
  }

  @Override
  public String visit(AndNode andNode, List<String> field) {
    return andNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.joining(LogicalOperator.AND.toString()));
  }

  @Override
  public String visit(OrNode orNode, List<String> field) {
    return orNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.joining(LogicalOperator.OR.toString()));
  }

  @Override
  public String visit(ComparisonNode comparisonNode, List<String> field) {
    if (field.stream().anyMatch(f -> comparisonNode.getSelector().equalsIgnoreCase(f))) {
      List<String> mappedArguments = comparisonNode.getArguments()
        .stream()
        .map(s -> ISODateTime.parse(s).getLocalDateTime().toString())
        .collect(Collectors.toList());
      return new ComparisonNode(comparisonNode.getOperator(), comparisonNode.getSelector(), mappedArguments)
        .toString();
    } else {
      return comparisonNode.toString();
    }
  }
}
