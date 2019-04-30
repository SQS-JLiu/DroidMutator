package mjava.op.operator;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Unary Operator replacement; e.g., Unary operations plus substitutions,  i++ -->  i+=2
 * @author jian liu
 */
public class UOR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(UOR.class);
    public UOR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(UnaryExpr m, Object obj){
                super.visit(m,obj);
                if(skipMutation(m)){
                    return;
                }
                try{
                    if(m.getOperator().equals(UnaryExpr.Operator.POSTFIX_INCREMENT) ||
                            m.getOperator().equals(UnaryExpr.Operator.PREFIX_INCREMENT)){
                        genMutants(m);
                    }
                } catch (Exception e){
                    System.err.println("UPR: No value present!!!");
                }
            }
        },null);
    }

    private void genMutants(UnaryExpr expr){
        AssignExpr mutant;
        mutant = new AssignExpr(expr.getExpression(),new IntegerLiteralExpr("2"),AssignExpr.Operator.PLUS);
        outputToFile(expr,mutant);
    }

    /**
     * Output UPR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(UnaryExpr original, AssignExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("UOR");
        String mutant_dir = getMuantID("UOR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            UOR_Writer writer = new UOR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("UOR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
