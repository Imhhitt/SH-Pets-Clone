package dev.smartshub.shpets.api.pet.action.ability.conditional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CompiledCondition {
    private static final Pattern CONDITION_PATTERN = Pattern.compile("(.+?)(>=|<=|!=|=|>|<)(.+)");
    private static final char AND = '&', OR = '|', XOR = '^';

    private final String expression;
    private final String left;
    private final String operator;
    private final String right;

    private final List<CompiledCondition> sub;
    private final LogicType logicType;

    private enum LogicType { AND, OR, XOR }

    private CompiledCondition(String expression, String left, String operator, String right) {
        this.expression = expression;
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.sub = null;
        this.logicType = null;
    }

    private CompiledCondition(String expression, List<CompiledCondition> sub, LogicType type) {
        this.expression = expression;
        this.left = this.operator = this.right = null;
        this.sub = sub;
        this.logicType = type;
    }

    public static CompiledCondition compile(String expr) {
        final String expressionLiteral = expr;
        expr = expr.trim();

        if (expr.indexOf(AND) != -1) {
            List<CompiledCondition> list = new ArrayList<>();
            for (String part : expr.split("&"))
                list.add(compile(part.trim()));
            return new CompiledCondition(expressionLiteral, list, LogicType.AND);
        }
        if (expr.indexOf(OR) != -1) {
            List<CompiledCondition> list = new ArrayList<>();
            for (String part : expr.split("\\|"))
                list.add(compile(part.trim()));
            return new CompiledCondition(expressionLiteral,list, LogicType.OR);
        }
        if (expr.indexOf(XOR) != -1) {
            List<CompiledCondition> list = new ArrayList<>();
            for (String part : expr.split("\\^"))
                list.add(compile(part.trim()));
            return new CompiledCondition(expressionLiteral,list, LogicType.XOR);
        }

        final Matcher matcher = CONDITION_PATTERN.matcher(expr);
        if (!matcher.matches()) {
            return new CompiledCondition(expressionLiteral, expr, null, null);
        }

        return new CompiledCondition(expressionLiteral,
            matcher.group(1).trim(),
            matcher.group(2).trim(),
            matcher.group(3).trim()
        );
    }

    public boolean evaluate(StringParser parser) {
        if (sub != null && logicType != null) {
            return switch (logicType) {
                case AND -> {
                    for (CompiledCondition c : sub) if (!c.evaluate(parser)) yield false;
                    yield true;
                }
                case OR -> {
                    for (CompiledCondition c : sub) if (c.evaluate(parser)) yield true;
                    yield false;
                }
                case XOR -> {
                    boolean res = false;
                    for (CompiledCondition c : sub) res ^= c.evaluate(parser);
                    yield res;
                }
            };
        }

        if (operator == null) return false;

        final String l = parser.parse(left);
        final String r = parser.parse(right);

        final Double ln = tryParseDouble(l);
        final Double rn = tryParseDouble(r);

        if (ln != null && rn != null) {
            return switch (operator) {
                case "=" -> ln.equals(rn);
                case "!=" -> !ln.equals(rn);
                case ">" -> ln > rn;
                case "<" -> ln < rn;
                case ">=" -> ln >= rn;
                case "<=" -> ln <= rn;
                default -> false;
            };
        }

        return switch (operator) {
            case "=" -> l.equalsIgnoreCase(r);
            case "!=" -> !l.equalsIgnoreCase(r);
            case "==" -> l.equals(r);
            case "!==" -> !l.equals(r);
            default -> false;
        };
    }

    private static Double tryParseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    @FunctionalInterface
    public interface StringParser {
        String parse(String input);
    }

    @Override
    public String toString() {
        return expression;
    }
}