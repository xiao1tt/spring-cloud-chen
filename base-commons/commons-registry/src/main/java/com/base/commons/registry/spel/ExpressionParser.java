package com.base.commons.registry.spel;

import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author chenxiaotong
 */
public class ExpressionParser {

    private static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    public static Object parseExpression(Map<String, Object> context, String expression) {

        EvaluationContext evaluationContext = new StandardEvaluationContext();

        context.forEach(evaluationContext::setVariable);

        return EXPRESSION_PARSER.parseExpression(expression).getValue(evaluationContext);
    }
}
