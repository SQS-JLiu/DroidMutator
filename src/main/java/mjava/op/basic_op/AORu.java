package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
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
 * Generate AORU (Arithmetic Operator Replacement (Unary)) mutants --
 * replace each occurrence of one of the arithmetic operators + and -
 * by each of the other operators
 */
public class AORu extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(AORu.class);
    private boolean switchStmtFlag = false;
    private Set<String> caseLabel = Sets.newHashSet();

    public AORu(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(UnaryExpr ue, Object arg) {
                super.visit(ue, arg);
                if (skipMutation(ue)) {
                    return;
                }
                genBasicUnaryMutants(ue);
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

    private void genBasicUnaryMutants(UnaryExpr ue) {
        //sometimes, switch(xxx)  case +1: xxx ; case -1: xxx; After the mutation, the compilation failed.
        UnaryExpr.Operator uop = ue.getOperator();
        if(switchStmtFlag && caseLabel.contains(ue.toString())){
            if (uop.equals(UnaryExpr.Operator.PLUS)) {
                String temp = ue.toString().replace(UnaryExpr.Operator.PLUS.asString(), "");
                if(caseLabel.contains(UnaryExpr.Operator.MINUS.asString()+temp)){
                    return;
                }
            }
            if(uop.equals(UnaryExpr.Operator.MINUS)){
                String temp = ue.toString().replace(UnaryExpr.Operator.MINUS.asString(), "");
                String temp2 = UnaryExpr.Operator.PLUS.asString() + temp;
                if(caseLabel.contains(temp) || caseLabel.contains(temp2)){
                    return;
                }
            }
        }
        UnaryExpr mutant;
        if (uop.equals(UnaryExpr.Operator.MINUS)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.PLUS);
            outputToFile(ue, mutant);
        } else if (uop.equals(UnaryExpr.Operator.PLUS)) {
            mutant = ue.clone();
            mutant.setOperator(UnaryExpr.Operator.MINUS);
            outputToFile(ue, mutant);
        }
    }

    /**
     * Output AORu mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, UnaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("AORu");
        String mutant_dir = getMuantID("AORu");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AORu_Writer writer = new AORu_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("AORu: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
