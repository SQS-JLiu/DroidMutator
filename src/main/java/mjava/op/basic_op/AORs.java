package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cited from muJava :
 * Generate AORS (Arithmetic Operator Replacement (Short-cut)) mutants --
 * replace each occurrence of the increment and decrement operators
 * by each of the other operators
 * Example: for (int i=0; i<length; i++) is mutated to for (int i=0; i<length; i--)
 */
public class AORs extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AORs.class);

    public AORs(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(UnaryExpr ue, Object arg) {
                super.visit(ue, arg);
                if (skipMutation(ue)) {
                    return;
                }
                UnaryExpr.Operator uop = ue.getOperator();
                if(uop.equals(UnaryExpr.Operator.POSTFIX_INCREMENT) || uop.equals(UnaryExpr.Operator.PREFIX_INCREMENT) ||
                        uop.equals(UnaryExpr.Operator.POSTFIX_DECREMENT) || uop.equals(UnaryExpr.Operator.PREFIX_DECREMENT)){
                    genShortCutUnaryMutants(ue,uop);
                }
            }
        }, null);
    }

    private void genShortCutUnaryMutants(UnaryExpr ue, UnaryExpr.Operator uop) {
        UnaryExpr mutant;
        if (!uop.equals(UnaryExpr.Operator.POSTFIX_INCREMENT)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.POSTFIX_INCREMENT);
            outputToFile(ue, mutant);
        } else if (!uop.equals(UnaryExpr.Operator.PREFIX_INCREMENT)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.PREFIX_INCREMENT);
            outputToFile(ue, mutant);
        } else if (!uop.equals(UnaryExpr.Operator.POSTFIX_DECREMENT)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.POSTFIX_DECREMENT);
            outputToFile(ue, mutant);
        } else if (!uop.equals(UnaryExpr.Operator.PREFIX_DECREMENT)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.PREFIX_DECREMENT);
            outputToFile(ue, mutant);
        }
    }

    /**
     * Output AORs mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, UnaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AORs");
        String mutant_dir = getMuantID("AORs");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AORs_Writer writer = new AORs_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("AORs: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
