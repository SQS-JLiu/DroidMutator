package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.AssignExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * Set visible attribute (from a View) to false
 * e.g. TextView emailTextView = (TextView) findViewById(R.id.EmailTextView);
 *      emailTextView.setVisibility(android.view.View.INVISIBLE);
 * Created by 2018/12/23
 * @author Jian Liu
 */
public class ViewComponentNotVisible extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(ViewComponentNotVisible.class);
    private boolean assignExprFlag = false;
    private AssignExpr targetExpr = null;
    private final String regx = "findViewById\\s*\\(\\s*(R\\.id\\.\\w+)\\s*\\)\\s*$";
    public ViewComponentNotVisible(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

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
                            //findViewById/(EditText)findViewById/(xxx)findViewById
                            String mutantStr = targetExpr.getTarget().toString() +".setVisibility(android.view.View.INVISIBLE)";
                            NameExpr mutant = new NameExpr(targetExpr.toString()+"; "+mutantStr);
                            outputToFile(targetExpr,mutant);
                        }
                    }
                }
                /**
                 * Case 1: compile failed: (solved)
                 * boolean isDualPane = findViewById(R.id.weather_info_container) != null;
                 *  ==> isDualPane.setVisibility(android.view.View.INVISIBLE)
                 */
                public void visit(AssignExpr assignExpr, Object obj) {
                    Pattern pattern = Pattern.compile(regx);
                    if(!pattern.matcher(assignExpr.toString()).find()){
                        //System.out.println(assignExpr.toString());
                        return;
                    }
                    assignExprFlag = true;
                    targetExpr = assignExpr;
                    super.visit(assignExpr,obj);
                    targetExpr = null;
                    assignExprFlag = false;
                }
            },null);
    }

    /**
     * Output ViewComponentNotVisible mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(AssignExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("ViewComponentNotVisible");
        String mutant_dir = getMuantID("ViewComponentNotVisible");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AssignExpr_Writer writer = new AssignExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("ViewComponentNotVisible: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
