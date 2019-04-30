package mjava.op.operator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The true and false results of the conditional expression become the same: e.g.,  a?b:c --> a?c:c
 *                                                                                 a?b:c --> a?b:b
 /**
 * Created by user on 2018/5/7.
  * @author Jian Liu
 */
public class CER extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(CER.class);

    public CER(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

     protected  void generateMutants(MethodDeclaration p){
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit(ConditionalExpr cex,Object obj){
                 super.visit(cex,obj);
                 if(skipMutation(cex)){
                     return;
                 }
                 true2False(cex);
                 false2true(cex);
                 exchangeConditional(cex);
             }
         },null);
     }

     private void exchangeConditional(ConditionalExpr meth){
         ConditionalExpr mutant;
         mutant =  meth.clone();
         mutant.setThenExpr(meth.getElseExpr());
         mutant.setElseExpr(meth.getThenExpr());
         outputToFile(meth, mutant);
     }

     private void true2False(ConditionalExpr meth) {
         ConditionalExpr mutant;
         mutant =  meth.clone();
         mutant.setElseExpr(meth.getThenExpr());
         outputToFile(meth, mutant);
     }

     private void false2true(ConditionalExpr meth) {
         ConditionalExpr mutant;
         mutant = meth.clone();
         mutant.setThenExpr(meth.getElseExpr());
         outputToFile(meth, mutant);
     }
    /**
     * Output CER mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(ConditionalExpr original, ConditionalExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("CER");
        String mutant_dir = getMuantID("CER");
        try {
            PrintWriter out = getPrintWriter(f_name);
            CER_Writer writer = new CER_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("CER: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
