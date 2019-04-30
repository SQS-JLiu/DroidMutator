package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.ExpressionWriter.MethodCallExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import mjava.util.StringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Randomly generate a different key in an Intent.putExtra(key, value) call
 * e.g. Intent.putExtra("key", value) ==> Intent.putExtra("xxx_key", value)
 * Created by 2018/12/23
 * @author Jian Liu
 */
public class InvalidKeyIntentPutExtra extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(InvalidKeyIntentPutExtra.class);

    public InvalidKeyIntentPutExtra(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(MethodCallExpr mc, Object obj) {
                super.visit(mc, obj);
                if (skipMutation(mc)) {
                    return;
                }
                //Intent.putExtra("key", value)
                if ("putExtra".equals(mc.getNameAsString()) && mc.getArguments().size() == 2) {
                    if(mc.getScope().isPresent()){
                        Expression callerExpr = mc.getScope().get();
                        if("android.content.Intent".equals(MutantsGenerator.getNodeType(callerExpr))){
                            genMutants(mc);
                        }
                    }
                }
            }
        }, null);
    }

    private void genMutants(MethodCallExpr original){
        MethodCallExpr mutant = original.clone();
        mutant.setArgument(0,new StringLiteralExpr(StringGenerator.generateRandomString()));
        outputToFile(original,mutant);
    }

    /**
     * Output InvalidKeyIntentPutExtra mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("InvalidKeyIntentPutExtra");
        String mutant_dir = getMuantID("InvalidKeyIntentPutExtra");
        try {
            PrintWriter out = getPrintWriter(f_name);
            MethodCallExpr_Writer writer = new MethodCallExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("InvalidKeyIntentPutExtra: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }

}
