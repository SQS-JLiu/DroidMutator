package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.GenericNodeWriter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * BuggyGUIListener (cited from MDroid+)
 * Description:
 * Assign null to a listener.
 * Code Example:
 * Before
 *  private View.OnClickListener listener = new View.OnClickListener() {
 *  *   @Override
 *     public void onClick(View view) {
 *       clicksCount += 1;
 *     }
 * }
 * After
 *  private View.OnClickListener listener = null;
 */
public class BuggyGUIListener extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(BuggyGUIListener.class);
    public BuggyGUIListener(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(VariableDeclarator varDclr, Object obj){
                if(skipMutation(varDclr)){ // Used to mutate statements with control dependencies
                    return;
                }
                if(varDclr.getInitializer().isPresent() &&
                        "View.OnClickListener".equals(varDclr.getTypeAsString())){
                        VariableDeclarator mutant = varDclr.clone();
                        mutant.setInitializer(new NullLiteralExpr());
                        outputToFile(varDclr,mutant);
                }
            }
        },null);
    }

    /**
     * Output BuggyGUIListener mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarator original, VariableDeclarator mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("BuggyGUIListener");
        String mutant_dir = getMuantID("BuggyGUIListener");
        try {
            PrintWriter out = getPrintWriter(f_name);
            GenericNodeWriter writer = new GenericNodeWriter(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("BuggyGUIListener: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
