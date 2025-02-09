package com.interpreter.lox;

public class Interpreter implements Expr.Visitor<Object>{

    public void interpret(Expr expression){
        try{
            Object result = evaluate(expression);
            System.out.println(stringify(result));
        } catch(RuntimeError error){
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type){
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield (double)left - (double)right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left / (double)right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left * (double)right;
            }
            case PLUS -> {
                Object result;
                if(left instanceof Double && right instanceof Double){
                    result = (double)left + (double)right;
                }
                else if(left instanceof String && right instanceof String){
                    result = left + (String)right;
                } else{
                    throw new RuntimeError(expr.operator, "Operands must be of type Number or Strings.");
                }
                yield result;
            }
            case LESS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left < (double)right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left <= (double)right;
            }
            case GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left > (double)right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left >= (double)right;
            }
            case EQUAL_EQUAL -> isEqual(left, right);
            case BANG_EQUAL -> !isEqual(left, right);
            default -> null;
        };
    }

    private String stringify(Object object) {
        if(object == null) return "nil";
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers");
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "must be a number.");
    }

    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null) return false;
        return a.equals(b);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        return switch (expr.operator.type) {
            case MINUS -> -(double) right;
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTruthy(Object object){
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean) object;
        return true;
    }
}
