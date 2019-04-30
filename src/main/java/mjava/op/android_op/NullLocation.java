package mjava.op.android_op;

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
 * NullGPSLocation (cited from MDroid+)
 * Description:
 * Inject a Null GPS location in the location services
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 * Location GPSLocation = new Location(provider);
 * After
 * Location GPSLocation = null;
 */
public class NullLocation extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(NullLocation.class);

    public NullLocation(CompilationUnit comp_unit, File originalFile) {
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
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("Location")) {
                Expression temp = var.getInitializer().get();
                var.setInitializer("null");
                outputToFile(varDlrExpr, mutant);
                var.setInitializer(temp);
            }
        }
    }

    /**
     * Output NullGPSLocation mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("NullLocation");
        String mutant_dir = getMuantID("NullLocation");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("NullGPSLocation: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
