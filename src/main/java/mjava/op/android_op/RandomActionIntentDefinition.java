package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Lists;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.ExpressionWriter.VarDlrExper_Writer;
import mjava.op.record.MethodLevelMutator;
import mjava.util.StringGenerator;
import mjava.util.XMLHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Replace the action argument in an Intent instantiation
 * e.g. Intent intent = new Intent("xxx.xxx.action"); ==> Intent intent = new Intent("xxx.xxx.action2")
 * Created by 2018/12/12
 * @author Jian Liu
 */
public class RandomActionIntentDefinition extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(RandomActionIntentDefinition.class);

    public RandomActionIntentDefinition(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(VariableDeclarationExpr varDlrExpr, Object obj) {
                super.visit(varDlrExpr, obj);
                if (skipMutation(varDlrExpr)) {
                    return;
                }
                genMutants(varDlrExpr);
            }
        }, null);
    }

    private void genMutants(VariableDeclarationExpr varDlrExpr) {
        // Intent intent = new Intent("xxx.xxx.action")
        VariableDeclarationExpr mutant = varDlrExpr.clone();
        for (VariableDeclarator var : mutant.getVariables()) {
            if (var.getInitializer().isPresent() && var.getTypeAsString().equals("Intent")) {
                Expression exp = var.getInitializer().get();
                if (exp.isObjectCreationExpr()) {
                    NodeList<Expression> nodeList = exp.asObjectCreationExpr().getArguments();
                    if (nodeList.size() == 1) {
                        Expression tempExp = nodeList.get(0);
                        String paramType = MutantsGenerator.getNodeType(tempExp);
                        if ("java.lang.String".equals(paramType)) {
                            Expression temp = var.getInitializer().get();
                            var.setInitializer("new Intent(\"" +getRandomAction()+ "\")");
                            outputToFile(varDlrExpr, mutant);
                            var.setInitializer(temp);
                        }
                    }
                }
            }
        }
    }

    private String getRandomAction() {
        JSONObject manifestJsonObj = new XMLHandler().readAndroidManifest();
        ArrayList<String> actionValue = Lists.newArrayList();
        if (manifestJsonObj.has("activity")) {
            JSONObject activitiseObj = manifestJsonObj.getJSONObject("activity");
            for (String key : activitiseObj.keySet()) {
                JSONObject tempObj = activitiseObj.getJSONObject(key);
                if(tempObj.has("action") && tempObj.getString("action") != null){
                    return  tempObj.getString("action");
                }
            }
        }
        return StringGenerator.generateRandomString();
    }

    /**
     * Output RandomActionIntentDefinition mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("RandomActionIntentDefinition");
        String mutant_dir = getMuantID("RandomActionIntentDefinition");
        try {
            PrintWriter out = getPrintWriter(f_name);
            VarDlrExper_Writer writer = new VarDlrExper_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("RandomActionIntentDefinition: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
