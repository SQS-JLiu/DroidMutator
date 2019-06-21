package mjava.op.java_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.util.BasicTypeUtil;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cited from muJava :
 * Generate AORb (Arithmetic Operator Replacement (Binary)) mutants --
 * replace an arithmetic new_op by each of the other operators
 * (*, /, %, +, -)
 */
public class AORb extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AORb.class);

    public AORb(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(BinaryExpr be, Object arg) {
                super.visit(be, arg);
                if (skipMutation(be)) {
                    return;
                }
                BinaryExpr.Operator bop = be.getOperator();
                if( bop.equals(BinaryExpr.Operator.PLUS)){
                    if(BasicTypeUtil.isObjectType(be.getLeft()) || BasicTypeUtil.isObjectType(be.getRight())){
                        return;
                    }
                }
                if(bop.equals(BinaryExpr.Operator.MULTIPLY) || bop.equals(BinaryExpr.Operator.DIVIDE) ||
                        bop.equals(BinaryExpr.Operator.REMAINDER) || bop.equals(BinaryExpr.Operator.PLUS) ||
                        bop.equals(BinaryExpr.Operator.MINUS)){
                    aorMutantGen(be,bop);
                }
            }

            public void visit(MethodCallExpr mce, Object arg){
                if(isContainSpecificMethod(mce.toString())){
                    return;
                }
                super.visit(mce, arg);
            }
        }, null);
    }

    private void aorMutantGen(BinaryExpr be,BinaryExpr.Operator bop) {
        BinaryExpr mutant;
        if (!bop.equals(BinaryExpr.Operator.MULTIPLY)) {
            mutant = be.clone();
            mutant.setOperator(BinaryExpr.Operator.MULTIPLY);
            outputToFile(be, mutant);
        }
        if (!bop.equals(BinaryExpr.Operator.DIVIDE)) {
            mutant = be.clone();
            mutant.setOperator(BinaryExpr.Operator.DIVIDE);
            outputToFile(be, mutant);
        }
        if (!bop.equals(BinaryExpr.Operator.REMAINDER)) {
            mutant = be.clone();
            mutant.setOperator(BinaryExpr.Operator.REMAINDER);
            outputToFile(be, mutant);
        }
        if (!bop.equals(BinaryExpr.Operator.PLUS)) {
            mutant = be.clone();
            mutant.setOperator(BinaryExpr.Operator.PLUS);
            outputToFile(be, mutant);
        }
        if (!bop.equals(BinaryExpr.Operator.MINUS)) {
            mutant = be.clone();
            mutant.setOperator(BinaryExpr.Operator.MINUS);
            outputToFile(be, mutant);
        }
    }

    /**
     * Output AORb mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AORb");
        String mutant_dir = getMuantID("AORb");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AORb_Writer writer = new AORb_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("AORb: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
