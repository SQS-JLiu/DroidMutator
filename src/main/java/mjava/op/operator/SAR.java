package mjava.op.operator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
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
 * String Argument Replacement (SAR): Replace the string
 * type parameter of a method call with an empty string.
 * @author Jian Liu
 * Created by user on 2018/9/5
 */
public class SAR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(SAR.class);
    public SAR(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit,originalFile);
    }

    protected  void generateMutants(MethodDeclaration p){
        p.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr me, Object arg){
                super.visit(me,arg);
                if(skipMutation(me)){
                    return;
                }
                if(isContainSpecificMethod(me.toString())){
                    return;
                }
                NodeList<Expression> exList =  me.getArguments();
                for(int i =0;i<exList.size();i++){
                    Expression ex = exList.get(i);
                    try{
                        if(ex.isMethodCallExpr()|| ex.isNameExpr() || ex.isArrayAccessExpr()){
                            ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(ex);
                            if((ex.isNameExpr() || ex.isArrayAccessExpr()) && t.describe().equals("java.lang.String")){
                                //StrArgRepMutantGen(me,ex,i);
                                StrRepMutantGen(me,i);
                            }
                        }
                    }
                    catch (Exception e){
                        //System.err.println(ex.toString()+" : Unsolved Symbol Exception!!!Ignore it...");
                    }
                }
            }
        }, null);
    }

    /**
     * @param me
     * @param pos
     */
    private void StrRepMutantGen(MethodCallExpr me,int pos){
        //System.out.println("start: "+me.getBegin().get().line +"  "+me.toString());
        MethodCallExpr mutant = me.clone();
        mutant.setArgument(pos,new StringLiteralExpr(""));
        outputToFile(me,mutant);
    }

    /**
     * Output SAR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("SAR");
        String mutant_dir = getMuantID("SAR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            SAR_Writer writer = new SAR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("SAR: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
