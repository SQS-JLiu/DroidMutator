package mjava.op.java_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.VarDlrExper_Writer;
import mjava.op.record.MethodLevelMutator;
import mjava.util.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * InvalidDate (cited from MDroid+)
 * Description:
 * Set a random Date to a date object
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 *  Date stdDate = new Date(year, month, date);
 * After
 *  Date stdDate = new Date(12345678910L);
 */
public class InvalidDate extends MethodLevelMutator{

    private static final Logger logger = LoggerFactory.getLogger(InvalidDate.class);

    public InvalidDate(CompilationUnit comp_unit, File originalFile) {
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
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("Date")) {
                Expression temp = var.getInitializer().get();
                if(temp.isObjectCreationExpr()){
                    Expression randInt = new IntegerLiteralExpr(TimestampGenerator.generateRandomTimestamp()+"L");
                    Expression initValue = temp.clone();
                    NodeList<Expression> nodeList = new NodeList<>();
                    nodeList.add(randInt);
                    initValue.asObjectCreationExpr().setArguments(nodeList);
                    var.setInitializer(initValue);
                    outputToFile(varDlrExpr, mutant);
                    //Restoring state for the next mutation
                    var.setInitializer(temp);
                }
            }
        }
    }

    /**
     * Output InvalidDate mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("InvalidDate");
        String mutant_dir = getMuantID("InvalidDate");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("InvalidDate: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
