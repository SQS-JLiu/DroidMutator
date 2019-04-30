package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.VarDlrExper_Writer;
import mjava.op.record.MethodLevelMutator;
import mjava.util.StringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * InvalidFilePath (cited from MDroid+)
 * Description:
 * Randomly mutate paths to files
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 *  File textFile = new File(“/sdcard/session.log”);
 * After
 *  File textFile = new File(“ecab6839856b426fbdae3e6e8c46c38c”);
 */
public class InvalidFilePath extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(InvalidFilePath.class);

    public InvalidFilePath(CompilationUnit comp_unit, File originalFile) {
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
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("File")) {
                Expression temp = var.getInitializer().get();
                if(temp.isObjectCreationExpr()){
                    Expression randStr = new StringLiteralExpr(StringGenerator.generateRandomString());
                    Expression initValue = temp.clone();
                    NodeList<Expression> nodeList = new NodeList<>();
                    nodeList.add(randStr);
                    initValue.asObjectCreationExpr().setArguments(nodeList);
                    var.setInitializer(initValue);
                    outputToFile(varDlrExpr, mutant);
                    var.setInitializer(temp);
                }
            }
        }
    }

    /**
     * Output InvalidFilePath mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("InvalidFilePath");
        String mutant_dir = getMuantID("InvalidFilePath");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("InvalidFilePath: Fails to create " + f_name);
        }
    }
}
