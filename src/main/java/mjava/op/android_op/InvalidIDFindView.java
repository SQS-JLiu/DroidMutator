package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.MethodCallExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import mjava.util.IntegerGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * InvalidIDFindView (cited from MDroid+)
 * Description:
 * Replace the id argument in an Activitity.findViewById call
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 *  TextView emailTextView = (TextView) findViewById(R.id.EmailTextView);
 * After
 *  TextView emailTextView = (TextView) findViewById(839);
 */
public class InvalidIDFindView extends MethodLevelMutator {
    private int MINIMUM = 1;
    private int MAXIMUM = 1000;

    private static final Logger logger = LoggerFactory.getLogger(InvalidIDFindView.class);

    public InvalidIDFindView(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(MethodCallExpr mc, Object obj){
                super.visit(mc,obj);
                if (skipMutation(mc)) {
                    return;
                }
                // findViewById(R.id.xxx)
                if("findViewById".equals(mc.getNameAsString())){
                    NodeList<Expression> expList =  mc.getArguments();
                    if(expList.size() == 1 && expList.get(0).toString().startsWith("R.id.")){
                        MethodCallExpr mutant = mc.clone();
                        int newID = IntegerGenerator.generateRandomInt(MINIMUM, MAXIMUM);
                        mutant.setArgument(0,new IntegerLiteralExpr(newID));
                        outputToFile(mc,mutant);
                    }
                }
            }
        }, null);
    }

    /**
     * Output InvalidIDFindView mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("InvalidIDFindView");
        String mutant_dir = getMuantID("InvalidIDFindView");
        try {
            PrintWriter out = getPrintWriter(f_name);
            MethodCallExpr_Writer writer = new MethodCallExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("InvalidIDFindView: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
