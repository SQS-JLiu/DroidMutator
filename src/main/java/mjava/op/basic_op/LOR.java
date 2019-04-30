package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cited from muJava:
 * Generate LOR (Logical Operator Replacement) mutants --
 * replace each occurrence of each bitwise logical operator
 * bitwise and-& ,bitwise or-|, exclusive or-^) by each of
 * the other operators
 */
public class LOR extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(LOR.class);

    public LOR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(BinaryExpr binExpr, Object obj) {
                super.visit(binExpr, obj);
                if (skipMutation(binExpr)) {
                    return;
                }
                BinaryExpr.Operator bop = binExpr.getOperator();
                if (bop.equals(BinaryExpr.Operator.BINARY_AND) || bop.equals(BinaryExpr.Operator.BINARY_OR)
                        || bop.equals(BinaryExpr.Operator.XOR)) {
                    lorMutantGen(binExpr, bop);
                }
            }
        }, null);
    }

    private void lorMutantGen(BinaryExpr binExpr, BinaryExpr.Operator bop) {
        BinaryExpr mutant;
        if (!bop.equals(BinaryExpr.Operator.BINARY_AND)) {
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.BINARY_AND);
            outputToFile(binExpr, mutant);
        } else if (!bop.equals(BinaryExpr.Operator.BINARY_OR)) {
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.BINARY_OR);
            outputToFile(binExpr, mutant);
        } else if (!bop.equals(BinaryExpr.Operator.XOR)) {
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.XOR);
            outputToFile(binExpr, mutant);
        }
    }

    /**
     * Output LOR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("LOR");
        String mutant_dir = getMuantID("LOR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            LOR_Writer writer = new LOR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("LOR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
