package mjava.op.new_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * String invocation method substitution: e.g., String url; url.startWith() <=>url.endwith()/url.contains()
 * /
/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class SCR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(SCR.class);

    public SCR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr m, Object obj){
                if(skipMutation(m)){
                    return;
                }
                if ("startsWith".equals(m.getNameAsString())) {
//            System.out.println("1. call::" +m.getName() );
                    endWithMutantGen(m);
                    containsMutantGen(m);
                }
                else if ("endsWith".equals(m.getNameAsString())) {
//             System.out.println("2. call::" + m.getName());
                    startWithMutantGen(m);
                    containsMutantGen(m);
                }
                else if ("contains".equals(m.getNameAsString())) {
//             System.out.println("3. call::" + m.getName());
                    startWithMutantGen(m);
                    endWithMutantGen(m);
                }
            }
        },null);
    }

    private void startWithMutantGen(MethodCallExpr meth) {
        MethodCallExpr mutant;
        mutant = meth.clone();
        mutant.setName("startsWith");
        outputToFile(meth, mutant);
    }

    private void endWithMutantGen(MethodCallExpr meth) {
        MethodCallExpr mutant;
        mutant = meth.clone();
        mutant.setName("endsWith");
        outputToFile(meth, mutant);
    }

    private void containsMutantGen(MethodCallExpr meth) {
        MethodCallExpr mutant;
        mutant = meth.clone();
        mutant.setName("contains");
        outputToFile(meth, mutant);
    }

    /**
     * Output SCR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("SCR");
        String mutant_dir = getMuantID("SCR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            SCR_Writer writer = new SCR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("SCR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
