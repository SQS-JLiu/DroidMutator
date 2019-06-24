package mjava.op.new_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * For loop mutation: for(int i=0;i<size;i++) -->  for(int i=0;i<size-1;i++) ,or for(int i=1;i<size;i++)
 */
 /**
 * Created by user on 2018/5/7.
  * @author Jian Liu
 */
public class FLR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(FLR.class);

     public FLR(CompilationUnit comp_unit, File originalFile) {
         super(comp_unit,originalFile);
     }

     protected  void generateMutants(MethodDeclaration p){
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit(ForStmt fs,Object obj){
                 super.visit(fs,obj);
                 if(skipMutation(fs)){
                     return;
                 }
                 initMutantGen(fs);
                 minusMutantGen(fs);
             }
         },null);
     }

     private void initMutantGen(ForStmt f){
         //Initialization mutation  int i=0; ==>int i=1;
         ForStmt fs =  f.clone();
         NodeList<Expression> nodeList =  fs.getInitialization();
         for(int i=0;i < nodeList.size(); i++){
             Expression ex = nodeList.get(i);
             if(ex.isVariableDeclarationExpr()){
                 VariableDeclarationExpr varDeclExpr = ex.asVariableDeclarationExpr();
                 NodeList<VariableDeclarator> varList = varDeclExpr.getVariables();
                 for(int j=0;j < varList.size();j++){
                     VariableDeclarator varDecl = varList.get(j);
                     if(varDecl.getTypeAsString().equals("int") && varDecl.getInitializer().isPresent()){
                         Expression ex2 = varDecl.getInitializer().get();
                         //System.out.println(varDecl.toString()+"  "+varDecl.getType().asString());
                         BinaryExpr binaryExpr = new BinaryExpr(ex2,new IntegerLiteralExpr(1), BinaryExpr.Operator.PLUS);
                         varDecl.setInitializer(binaryExpr);
                         outputToFile(f, fs);
                     }
                 }
             }
         }
     }

     private void minusMutantGen(ForStmt f) {
         //i<size mutate to i<size-1
         ForStmt fs =  f.clone();
         if(fs.getCompare().isPresent() && fs.getCompare().get().isBinaryExpr()
                 &&  existBinarySymbol(fs.getCompare().toString())){
             BinaryExpr be =(BinaryExpr)fs.getCompare().get();
             BinaryExpr beTemp = new BinaryExpr(be.getRight(), new IntegerLiteralExpr("1"),BinaryExpr.Operator.MINUS);
             be.setRight(beTemp);
             outputToFile(f, fs);
         }
     }

     private boolean existBinarySymbol(String str){
         if(str.contains("<") || str.contains(">")){
             return true;
         }
         return false;
     }


     /**
      * Output FLR mutants to files
      *
      * @param original
      * @param mutant
      */
     public void outputToFile(ForStmt original, ForStmt mutant) {
         if (comp_unit == null || currentMethodSignature == null){
             return;
         }
         num++;
         String f_name = getSourceName("FLR");
         String mutant_dir = getMuantID("FLR");
         try {
             PrintWriter out = getPrintWriter(f_name);
             FLR_Writer writer = new FLR_Writer(mutant_dir, out);
             writer.setMutant(original, mutant);
             writer.setMethodSignature(currentMethodSignature);
             writer.writeFile(original_file);
             out.flush();
             out.close();
         }
         catch (IOException e) {
             System.err.println("FLR: Fails to create " + f_name);
             logger.error("Fails to create " + f_name);
         }
     }
}
