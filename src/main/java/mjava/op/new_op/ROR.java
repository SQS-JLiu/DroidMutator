package mjava.op.new_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.util.BasicTypeUtil;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *    Generate ROR (Relational Operator Replacement) mutants --
 *    replace each occurrence of one of the relational operators
 *    (<, <=, >, >=, =, <>) by each of the other operators
 *    and by falseOp and trueOp where  falseOp always returns false and
 *    trueOp always returns true
 */
/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class ROR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(ROR.class);

    public ROR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }


    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(BinaryExpr be, Object obj){
                super.visit(be,obj);
                if(skipMutation(be)){
                    return;
                }
                if(BasicTypeUtil.isCompareOperator(be.getOperator())){
                    if(BasicTypeUtil.isArithmeticType(be.getLeft()) && BasicTypeUtil.isArithmeticType(be.getRight())){
                        primitiveMutantGen(be);
                    }else {
                        objectMutantGen(be);
                    }
                }
            }
        },null);
    }

    private void primitiveMutantGen(BinaryExpr binEx){
        BinaryExpr mutantBinEx;
        BinaryExpr.Operator operator = binEx.getOperator();
        if(operator != BinaryExpr.Operator.LESS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.LESS);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.LESS_EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.LESS_EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.NOT_EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.NOT_EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.GREATER){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.GREATER);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.GREATER_EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
    }

    private void objectMutantGen(BinaryExpr binEx){
        BinaryExpr mutantBinEx;
        BinaryExpr.Operator operator = binEx.getOperator();
        if(operator != BinaryExpr.Operator.EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
        if(operator != BinaryExpr.Operator.NOT_EQUALS){
            mutantBinEx = binEx.clone();
            mutantBinEx.setOperator(BinaryExpr.Operator.NOT_EQUALS);
            outputToFile(binEx,mutantBinEx);
        }
    }

    /**
     * Output ROR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("ROR");
        String mutant_dir = getMuantID("ROR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            ROR_Writer writer = new ROR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("ROR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
