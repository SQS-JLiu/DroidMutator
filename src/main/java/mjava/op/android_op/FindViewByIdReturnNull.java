package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.MethodCallExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Assign a variable (returned by Activity.findViewById) to null
 * e.g. button = findViewById(R.id.xxx); ==> button = null;
 * Created by 2018/12/23
 * @author Jian Liu
 */
public class FindViewByIdReturnNull extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(FindViewByIdReturnNull.class);
    public FindViewByIdReturnNull(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }
    private boolean assignExprFlag = false;

    protected void generateMutants(MethodDeclaration p) {
            p.accept(new VoidVisitorAdapter<Object>() {
                public void visit(MethodCallExpr mc, Object obj) {
                    super.visit(mc,obj);
                    if(!assignExprFlag){
                        return;
                    }
                    if (skipMutation(mc)) {
                        return;
                    }
                    if("findViewById".equals(mc.getNameAsString())){
                        NodeList<Expression> expList =  mc.getArguments();
                        if(expList.size() == 1 && expList.get(0).toString().startsWith("R.id.")){
                            outputToFile(mc,new NullLiteralExpr());
                        }
                    }
                }
                public void visit(AssignExpr assignExpr, Object obj) {
                    assignExprFlag = true;
                    super.visit(assignExpr,obj);
                    assignExprFlag = false;
                }
            },null);
    }

    /**
     * Output FindViewByIdReturnNull mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, NullLiteralExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("FindViewByIdReturnNull");
        String mutant_dir = getMuantID("FindViewByIdReturnNull");
        try {
            PrintWriter out = getPrintWriter(f_name);
            MethodCallExpr_Writer writer = new MethodCallExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("FindViewByIdReturnNull: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
