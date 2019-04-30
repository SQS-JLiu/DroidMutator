package mjava.op.basic_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cited from muJava :
 * Generate ASRS (Assignment Operator Replacement (short-cut)) mutants --
 * replace each occurrence of one of the assignment operators
 * (+=, -+, *=, /=, %=, &=, |=, ^=, <<=, >>=, >>>=) by each of the
 * other operators
 */
public class ASR extends MethodLevelMutator{
    private static final Logger logger = LoggerFactory.getLogger(ASR.class);

    public ASR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(AssignExpr assignExpr, Object obj) {
                super.visit(assignExpr, obj);
                if (skipMutation(assignExpr)) {
                    return;
                }
                AssignExpr.Operator aop = assignExpr.getOperator();
                if(aop.equals(AssignExpr.Operator.PLUS) || aop.equals(AssignExpr.Operator.MINUS) ||
                        aop.equals(AssignExpr.Operator.MULTIPLY) || aop.equals(AssignExpr.Operator.DIVIDE) ||
                        aop.equals(AssignExpr.Operator.REMAINDER)){
                    if(aop.equals(AssignExpr.Operator.PLUS)){
                        String targetType = MutantsGenerator.getNodeType(assignExpr.getTarget());
                        if("java.lang.String".equals(targetType)){   // String a="abc"; a+=1 /  a+="def";
                            return;
                        }
                    }
                    genArithmeticMutants(assignExpr,aop);
                }else if(aop.equals(AssignExpr.Operator.BINARY_AND) || aop.equals(AssignExpr.Operator.BINARY_OR) ||
                        aop.equals(AssignExpr.Operator.XOR)){
                    genLogicalMutants(assignExpr,aop);
                }else if(aop.equals(AssignExpr.Operator.LEFT_SHIFT) || aop.equals(AssignExpr.Operator.SIGNED_RIGHT_SHIFT)
                        || aop.equals(AssignExpr.Operator.UNSIGNED_RIGHT_SHIFT)){
                    genShiftMutants(assignExpr,aop);
                }
            }
        }, null);
    }

    private void genArithmeticMutants(AssignExpr assignExpr, AssignExpr.Operator aop){
        AssignExpr mutant;
        if(!aop.equals(AssignExpr.Operator.PLUS)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.PLUS);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.MINUS)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.MINUS);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.MULTIPLY)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.MULTIPLY);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.DIVIDE)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.DIVIDE);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.REMAINDER)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.REMAINDER);
            outputToFile(assignExpr, mutant);
        }
    }

    private void genLogicalMutants(AssignExpr assignExpr, AssignExpr.Operator aop){
        AssignExpr mutant;
        if(!aop.equals(AssignExpr.Operator.BINARY_AND)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.BINARY_AND);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.BINARY_OR)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.BINARY_OR);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.XOR)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.XOR);
            outputToFile(assignExpr, mutant);
        }
    }

    private void genShiftMutants(AssignExpr assignExpr, AssignExpr.Operator aop){
        AssignExpr mutant;
        if(!aop.equals(AssignExpr.Operator.LEFT_SHIFT)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.LEFT_SHIFT);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.SIGNED_RIGHT_SHIFT)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.SIGNED_RIGHT_SHIFT);
            outputToFile(assignExpr, mutant);
        }
        if(!aop.equals(AssignExpr.Operator.UNSIGNED_RIGHT_SHIFT)){
            mutant = assignExpr.clone();
            mutant.setOperator(AssignExpr.Operator.UNSIGNED_RIGHT_SHIFT);
            outputToFile(assignExpr, mutant);
        }
    }

    /**
     * Output ASR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(AssignExpr original, AssignExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("ASR");
        String mutant_dir = getMuantID("ASR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            ASR_Writer writer = new ASR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("ASR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
