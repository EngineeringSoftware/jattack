package jattack.ast.operator;

public enum IncDecOp implements Op{
    PRE_INC("++");

    private final String strRep;

    IncDecOp(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String asStr() {
        return strRep;
    }

    public Number apply(Number operand) {
        if (operand instanceof Integer) {
            return apply(operand.intValue());
        } else if (operand instanceof Long) {
            return apply(operand.longValue());
        } else if (operand instanceof Double) {
            return apply(operand.doubleValue());
        } else {
            throw new RuntimeException("Unsupported type: " + operand.getClass());
        }
    }

    private int apply(int operand) {
        ++operand;
        return operand;
    }

    private long apply(long operand) {
        ++operand;
        return operand;
    }

    private double apply(double operand) {
        ++operand;
        return operand;
    }
}
