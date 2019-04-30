package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.ExpressionWriter.MethodCallExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Replace the parameter with the new object in an Intent.putExtras() call
 * e.g. intent.putExtras(bundle/intent) ==> intent.putExtras(new Bundle()/new Intent())
 * created by 2018/12/22
 * @author Jian Liu
 */
public class NewParamIntentPutExtras extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(NewParamIntentPutExtras.class);

    public NewParamIntentPutExtras(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(MethodCallExpr mc, Object obj) {
                super.visit(mc, obj);
                if (skipMutation(mc)) {
                    return;
                }
                //intent.putExtras(bundle/intent)   android.os.Bundle/android.content.Intent
                if ("putExtras".equals(mc.getNameAsString()) && mc.getArguments().size() == 1) {
                    if(mc.getScope().isPresent()){
                        Expression callerExpr = mc.getScope().get();
                        String callerType = MutantsGenerator.getNodeType(callerExpr);
                        if("android.content.Intent".equals(callerType)){
                            String paramType = MutantsGenerator.getNodeType(mc.getArguments().get(0));
                            if("android.content.Intent".equals(paramType)||"android.os.Bundle".equals(paramType)){
                                genMutants(mc,paramType);
                            }
                        }
                    }
                }
            }
        }, null);
    }

    private void genMutants(MethodCallExpr original,String paramType) {
        MethodCallExpr mutant = original.clone();
        mutant.setArgument(0,new NameExpr("new "+paramType+"()"));
        outputToFile(original,mutant);
    }

    /**
     * Output NewParamIntentPutExtras mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("NewParamIntentPutExtras");
        String mutant_dir = getMuantID("NewParamIntentPutExtras");
        try {
            PrintWriter out = getPrintWriter(f_name);
            MethodCallExpr_Writer writer = new MethodCallExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("NewParamIntentPutExtras: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
