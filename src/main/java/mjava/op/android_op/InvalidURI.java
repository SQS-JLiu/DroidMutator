package mjava.op.android_op;

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
 * InvalidURI (cited from MDroid+)
 * Description:
 * If URIs are used internally, randomly mutate the URIs
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 *  URI uri = new URI(u.toString());
 * After
 *  URI uri = new URI(“ecab6839856b426fbdae3e6e8c46c38c”);
 */
public class InvalidURI extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(InvalidURI.class);

    public InvalidURI(CompilationUnit comp_unit, File originalFile) {
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
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("URI")) {
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
     * Output InvalidURI mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("InvalidURI");
        String mutant_dir = getMuantID("InvalidURI");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("InvalidURI: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
