package mjava.op.operator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Integer Argument Replacement (IAR): Replace the integer
 * type parameter of a method call with 0
 * @author Jian Liu
 * Created by user on 2018/9/10
 */
public class IAR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(IAR.class);
    public IAR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr me, Object arg){
                super.visit(me,arg);
                //System.out.println("IAR running====="+me.toString());
                if(skipMutation(me)){
                    return;
                }
                if(isContainSpecificMethod(me.toString())){
                    return;
                }
                //System.out.println("IAR running=====");
                NodeList<Expression> exList =  me.getArguments();
                for(int i =0;i<exList.size();i++){
                    Expression ex = exList.get(i);
                    try{
                        if(ex.isMethodCallExpr()|| ex.isNameExpr() || ex.isArrayAccessExpr()){
                            ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(ex);
                            if(t.describe().equals("int")|| t.describe().equals("java.lang.Long")){
                                genArgRepMutantGen(me,ex,i);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.err.println(ex.toString()+" : Unsolved Symbol Exception!!!Ignore it...");
                    }
                }
            }
        }, null);
    }

    private void genArgRepMutantGen(MethodCallExpr me,Expression ex,int pos){
        //原数据替换为0
        MethodCallExpr mutantZero = me.clone();
        IntegerLiteralExpr literalExpr = new IntegerLiteralExpr(0);
        mutantZero.setArgument(pos,literalExpr);
        outputToFile(me,mutantZero);

        //原数据替换为原数据+原数据
        MethodCallExpr mutantPlus = me.clone();
        //IntegerLiteralExpr literalExpr1 = new IntegerLiteralExpr(1);
        BinaryExpr binaryExpr = new BinaryExpr(ex,ex, BinaryExpr.Operator.PLUS);
        mutantPlus.setArgument(pos,binaryExpr);
        outputToFile(me,mutantPlus);

        //原数据替换为原数据*原数据
        MethodCallExpr mutantMulti = me.clone();
        //IntegerLiteralExpr literalExpr2 = new IntegerLiteralExpr(1);
        BinaryExpr binaryExpr2 = new BinaryExpr(ex,ex, BinaryExpr.Operator.MULTIPLY);
        mutantMulti.setArgument(pos,binaryExpr2);
        outputToFile(me,mutantMulti);
    }

    /**
     * Output IAR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("IAR");
        String mutant_dir = getMuantID("IAR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            IAR_Writer writer = new IAR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("IAR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
