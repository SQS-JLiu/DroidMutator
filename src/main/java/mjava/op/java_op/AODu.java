package mjava.op.java_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Sets;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * cited from muJava :
 * Generate AODU (Arithmetic Operator Deletion (Unary)) mutants --
 * delete a unary new_op (arithmetic -) before each variable or expression
 */
public class AODu extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AODu.class);
    private boolean switchStmtFlag = false;
    private Set<String> caseLabel = Sets.newHashSet();

    public AODu(CompilationUnit comp_unit, File originalFile) {
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

            public void visit(SwitchStmt ss, Object arg) {
                switchStmtFlag = true;
                for (SwitchEntryStmt entry : ss.getEntries()) {
                    if (entry.getLabel().isPresent()) {
                        caseLabel.add(entry.getLabel().get().toString());
                    }
                }
                super.visit(ss, arg);
                switchStmtFlag = false;
                caseLabel.clear();
            }
        }, null);
    }

    private void generateDeletionMutants(UnaryExpr ue) {
        //sometimes, switch(xxx)  case +1: xxx ; case -1: xxx; After the mutation, the compilation failed.
        UnaryExpr.Operator uop = ue.getOperator();
        if(switchStmtFlag && caseLabel.contains(ue.toString())){
            if (uop.equals(UnaryExpr.Operator.PLUS)) {
                String temp = ue.toString().replaceFirst(UnaryExpr.Operator.PLUS.asString(), "");
                if(caseLabel.contains(UnaryExpr.Operator.MINUS.asString()+temp)){
                    return;
                }
            }
            if(uop.equals(UnaryExpr.Operator.MINUS)){
                String temp = ue.toString().replaceFirst(UnaryExpr.Operator.MINUS.asString(), "");
                String temp2 = UnaryExpr.Operator.PLUS.asString() + temp;
                if(caseLabel.contains(temp) || caseLabel.contains(temp2)){
                    return;
                }
            }
        }
        NameExpr mutant;
        if (uop.equals(UnaryExpr.Operator.PLUS)) {
            String temp = ue.toString().replaceFirst(UnaryExpr.Operator.PLUS.asString(), "");
            mutant = new NameExpr(temp);
            outputToFile(ue, mutant);
        } else if (uop.equals(UnaryExpr.Operator.MINUS)) {
            mutant = new NameExpr(ue.toString().replaceFirst(UnaryExpr.Operator.MINUS.asString(), ""));
            outputToFile(ue, mutant);
        }
    }

    /**
     * Output AODu mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AODu");
        String mutant_dir = getMuantID("AODu");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AODu_Writer writer = new AODu_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("AODu: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
