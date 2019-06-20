package mjava.op.new_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.BasicTypeUtil;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *    Generate COR (Conditional Operator Replacement mutants --
 *    replace each logical new_op by each of the other operators
 *    (and-&&, or-||, and with no conditional evaluation-&,
 *    or with no conditional evaluation-|, not equivalent-^)
 */
/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class COR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(COR.class);

    public COR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(BinaryExpr be,Object obj){
                if(skipMutation(be)){
                    return;
                }
                if(BasicTypeUtil.isLogicalOperator(be.getOperator())){
                    injectError(be);
                }
                super.visit(be,obj);
            }
        },null);
    }

    private void injectError(BinaryExpr binEx){
        //in order generate effective injection error, we generate specific mutants
        BinaryExpr mutantBinEx;
        BinaryExpr.Operator operator = binEx.getOperator();
        if(operator == BinaryExpr.Operator.AND){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.OR);
            outputToFile(binEx,mutantBinEx);
        }
        else if(operator == BinaryExpr.Operator.OR){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.AND);
            outputToFile(binEx,mutantBinEx);
        }
    }

    private void mutantGen(BinaryExpr binEx){
        BinaryExpr mutantBinEx;
        BinaryExpr.Operator operator = binEx.getOperator();
        if((operator != BinaryExpr.Operator.AND) && (operator != BinaryExpr.Operator.BINARY_AND)){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.AND);
            outputToFile(binEx,mutantBinEx);
        }
        if((operator != BinaryExpr.Operator.OR) && (operator != BinaryExpr.Operator.BINARY_OR)){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.OR);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.XOR){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.XOR);
            outputToFile(binEx,mutantBinEx);
        }
    }


    /**
     * Output COR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("COR");
        String mutant_dir = getMuantID("COR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            COR_Writer writer = new COR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("COR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
