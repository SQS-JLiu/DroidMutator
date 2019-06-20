package mjava.op.java_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.VarDlrExper_Writer;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * NullBackEndServiceReturn (cited from MDroid+)
 * Description:
 * Assign null to a response variable from a back-end service
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 *  HttpResponse response = client.execute(httpGet);
 * After
 *  HttpResponse response = null;
 */
public class NullBackEndServiceReturn extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(NullBackEndServiceReturn.class);

    public NullBackEndServiceReturn(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(VariableDeclarationExpr varDlrExpr, Object obj) {
                super.visit(varDlrExpr,obj);
                if (skipMutation(varDlrExpr)) {
                    return;
                }
                genMutants(varDlrExpr);
            }
        }, null);
    }

    private void genMutants(VariableDeclarationExpr varDlrExpr) {
        VariableDeclarationExpr mutant = varDlrExpr.clone();
        for (VariableDeclarator var : mutant.getVariables()) {
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("HttpResponse")) {
                Expression temp = var.getInitializer().get();
                var.setInitializer("null");
                outputToFile(varDlrExpr, mutant);
                var.setInitializer(temp);
            }
        }
    }

    /**
     * Output NullBackEndServiceReturn mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("NullBackEndServiceReturn");
        String mutant_dir = getMuantID("NullBackEndServiceReturn");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("NullBackEndServiceReturn: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
