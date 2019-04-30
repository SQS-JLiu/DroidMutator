package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cited from muJava :
 * Generate AODS (Arithmetic Operator Deletion (Short-cut)) mutants --
 * delete each occurrence of an increment operator (++) or a decrement
 * operator (--)
 */
public class AODs extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AODs.class);
    private boolean flag = false;
    public AODs(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(UnaryExpr ue, Object arg) {
                super.visit(ue, arg);
                if (skipMutation(ue)) {
                    return;
                }
                generateDeletionMutants(ue);
            }

            public void visit(ForStmt fs, Object arg) {
                // Do not consider conditions for "FOR STMT"
                fs.getBody().accept(this,arg);
            }

            public void visit(BinaryExpr be, Object arg){
                flag = true;
                super.visit(be,arg);
                flag = false;
            }

            public void visit(AssignExpr ae, Object arg){
                flag = true;
                super.visit(ae,arg);
                flag = false;
            }
        }, null);
    }

    private void generateDeletionMutants(UnaryExpr ue) {
        UnaryExpr.Operator uop = ue.getOperator();
        NameExpr mutant;
        if (uop.equals(UnaryExpr.Operator.PREFIX_DECREMENT) || uop.equals(UnaryExpr.Operator.POSTFIX_DECREMENT) ||
                uop.equals(UnaryExpr.Operator.PREFIX_INCREMENT) || uop.equals(UnaryExpr.Operator.POSTFIX_INCREMENT)) {
            if(flag){
                mutant = new NameExpr(ue.toString().replace(uop.asString(),""));
                outputToFile(ue, mutant);
            }
        }
    }

    /**
     * Output AODs mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AODs");
        String mutant_dir = getMuantID("AODs");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AODs_Writer writer = new AODs_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("AODs: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
