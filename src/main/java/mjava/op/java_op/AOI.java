package mjava.op.java_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.GenericWriter;
import mjava.op.record.MethodLevelMutator;
import mjava.util.BasicTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Generate AOI (Arithmetic Operator Insertion) mutants :
 *  *    insert arithmetic operator - before
 *  *    insert unary operators (increment ++, decrement --) before and after
 *  *    each variable of an arithmetic type
 * @author Jian Liu
 * @version 1.0
 */
public class AOI extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AOI.class);
    public AOI(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(BinaryExpr be, Object obj) {
                super.visit(be,obj);
                if(skipMutation(be)){ // Used to mutate statements with control dependencies
                    return;
                }
                genMutants(be);
            }

            public void visit(UnaryExpr ue, Object obj){
                //Ignore if it is a unary expression
            }
        },null);
    }

    private void genMutants(BinaryExpr be){
        Expression leftExp = be.getLeft();
        Expression rightExp = be.getRight();
        BinaryExpr mutant = be.clone();
        if(leftExp.isNameExpr() &&BasicTypeUtil.isArithmeticType(leftExp)){
            //We think the postfix is not working well. Code Example 1: return num1+num2++; ==>  return num1+num2++;
            // Code Example 2: if(a>b) return b; ==> if(a>b++) return b;
            // These ++ is meaningless. So Ignore the generation of this mutant.
            // case 1: a ==> a++
//            UnaryExpr unaryExpr = new UnaryExpr(leftExp.clone(),UnaryExpr.Operator.POSTFIX_INCREMENT);
//            mutant.setLeft(unaryExpr);
//            outputToFile(be,mutant);
            //case 2: a==> ++a
            UnaryExpr unaryExpr2 = new UnaryExpr(leftExp.clone(),UnaryExpr.Operator.PREFIX_INCREMENT);
            mutant.setLeft(unaryExpr2);
            outputToFile(be,mutant);
            // case 3: a ==> a--
//            UnaryExpr unaryExp3 = new UnaryExpr(leftExp.clone(),UnaryExpr.Operator.POSTFIX_DECREMENT);
//            mutant.setLeft(unaryExp3);
//            outputToFile(be,mutant);
            //case 4: a==> --a
            UnaryExpr unaryExpr4 = new UnaryExpr(leftExp.clone(),UnaryExpr.Operator.PREFIX_DECREMENT);
            mutant.setLeft(unaryExpr4);
            outputToFile(be,mutant);
            //case 5: a==> -a
            UnaryExpr unaryExpr5 = new UnaryExpr(leftExp.clone(),UnaryExpr.Operator.MINUS);
            mutant.setLeft(unaryExpr5);
            outputToFile(be,mutant);
        }
        mutant = be.clone();
        if(rightExp.isNameExpr() && BasicTypeUtil.isArithmeticType(rightExp)){
            // case 1: b ==> b++
//            UnaryExpr unaryExpr = new UnaryExpr(rightExp.clone(),UnaryExpr.Operator.POSTFIX_INCREMENT);
//            mutant.setRight(unaryExpr);
//            outputToFile(be,mutant);
            //case 2: b==> ++b
            UnaryExpr unaryExpr2 = new UnaryExpr(rightExp.clone(),UnaryExpr.Operator.PREFIX_INCREMENT);
            mutant.setRight(unaryExpr2);
            outputToFile(be,mutant);
            // case 1: b ==> b--
//            UnaryExpr unaryExpr3 = new UnaryExpr(rightExp.clone(),UnaryExpr.Operator.POSTFIX_DECREMENT);
//            mutant.setRight(unaryExpr3);
//            outputToFile(be,mutant);
            //case 2: b==> --b
            UnaryExpr unaryExpr4 = new UnaryExpr(rightExp.clone(),UnaryExpr.Operator.PREFIX_DECREMENT);
            mutant.setRight(unaryExpr4);
            outputToFile(be,mutant);
            //case 5: b==> -b
            UnaryExpr unaryExpr5 = new UnaryExpr(rightExp.clone(),UnaryExpr.Operator.MINUS);
            mutant.setRight(unaryExpr5);
            outputToFile(be,mutant);
        }
    }

    /**
     * Output AOI mutants to files
     * @param original
     * @param mutant
     */
    private void outputToFile(BinaryExpr original,BinaryExpr mutant){
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AOI");
        String mutant_dir = getMuantID("AOI");
        try {
            PrintWriter out = getPrintWriter(f_name);
            GenericWriter writer = new GenericWriter(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }catch (IOException ioe){
            System.err.println("AOI: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }

}
