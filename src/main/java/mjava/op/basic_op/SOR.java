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
 * cited from muJava :
 * Generate SOR (Shift Operator Replacement) mutants --
 * replace each occurrence of one of the shift operators <<, >>, and >>>
 * by each of the other operators
 */
public class SOR extends MethodLevelMutator{
    private static final Logger logger = LoggerFactory.getLogger(SOR.class);

    public SOR(CompilationUnit comp_unit, File originalFile) {
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
                if(bop.equals(BinaryExpr.Operator.LEFT_SHIFT) || bop.equals(BinaryExpr.Operator.SIGNED_RIGHT_SHIFT)
                        || bop.equals(BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT)){
                    sorMutantGen(binExpr,bop);
                }
            }
        }, null);
    }

    private void sorMutantGen(BinaryExpr binExpr, BinaryExpr.Operator bop){
        BinaryExpr mutant;
        if(!bop.equals(BinaryExpr.Operator.LEFT_SHIFT)){
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.LEFT_SHIFT);
            outputToFile(binExpr, mutant);
        }else if(!bop.equals(BinaryExpr.Operator.SIGNED_RIGHT_SHIFT)){
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.SIGNED_RIGHT_SHIFT);
            outputToFile(binExpr, mutant);
        }else if(!bop.equals(BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT)){
            mutant = binExpr.clone();
            mutant.setOperator(BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT);
            outputToFile(binExpr, mutant);
        }
    }

    /**
     * Output SOR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("SOR");
        String mutant_dir = getMuantID("SOR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            SOR_Writer writer = new SOR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("SOR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
