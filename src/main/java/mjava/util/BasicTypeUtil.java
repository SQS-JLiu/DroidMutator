package mjava.util;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import edu.ecnu.sqslab.mjava.MutantsGenerator;

/**
 * Created by user on 2018/5/8.
 *
 * @author Jian Liu
 */
public class BasicTypeUtil {

    /**
     * Determine whether a given expression is of arithmetic type
     *
     * @param expr
     * @return boolean
     */
    public static boolean isArithmeticType(Expression expr) {
        String type = MutantsGenerator.getNodeType(expr);
        //java.lang.Character  java.lang.Byte  java.lang.Short  java.lang.Integer
        //java.lang.Long  java.lang.Float  java.lang.Double
        if (type.equals("int") || type.equals("long") || type.equals("double")
                || type.equals("float") || type.equals("short") || type.equals("char")
                || type.equals("byte")) {
            return true;
        }
        if (type.equals("java.lang.Integer") || type.equals("java.lang.Long") || type.equals("java.lang.Double")
                || type.equals("java.lang.Float") || type.equals("java.lang.Short") || type.equals("java.lang.Character")
                || type.equals("java.lang.Byte")) {
            return true;
        }
        return false;
    }

    public static boolean isObjectType(Expression expr){
        String type = MutantsGenerator.getNodeType(expr);
        if("java.lang.String".equals(type)||"".equals(type) || !isArithmeticType(expr)){
            return true;
        }
        return false;
    }

    public static boolean isCompareOperator(BinaryExpr.Operator operator) {
        if (operator.equals(BinaryExpr.Operator.EQUALS) ||
                operator.equals(BinaryExpr.Operator.NOT_EQUALS) ||
                operator.equals(BinaryExpr.Operator.LESS) ||
                operator.equals(BinaryExpr.Operator.LESS_EQUALS) ||
                operator.equals(BinaryExpr.Operator.GREATER) ||
                operator.equals(BinaryExpr.Operator.GREATER_EQUALS)) {
            return true;
        }
        return false;
    }

    public static boolean isLogicalOperator(BinaryExpr.Operator operator) {
        if (operator.equals(BinaryExpr.Operator.AND) ||
                operator.equals(BinaryExpr.Operator.OR) ||
                operator.equals(BinaryExpr.Operator.BINARY_AND) ||
                operator.equals(BinaryExpr.Operator.BINARY_OR) ||
                operator.equals(BinaryExpr.Operator.XOR)) {
            return true;
        }
        return false;
    }
}
