package mjava.op.operator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * For loop mutation: for(int i=0;i<size;i++) --> for(int i=0;i<size;i=size)   /  for(int i=0;i<size-1;i++)
 *                                                for(int i=1;i<size;i++)
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
                 try{
                     if(fs.getCompare().get().isBinaryExpr() && existBinarySymbol(fs.getCompare().toString())){
                         //System.out.println(fs.toString());
                         initMutantGen(fs);
                         minusMutantGen(fs);
                         assignmentMutantGen(fs);
                     }
                 }
                 catch (NoSuchElementException e){
                     System.err.println("FLR: No value present!!!");
                 }
             }
         },null);
     }

     private void initMutantGen(ForStmt f){
         try{
             // 初始化变异  int i=0; ==>int  i= 0+1;
             ForStmt fs =  f.clone();
             NodeList<Expression> nodeList =  fs.getInitialization();
             for(int i=0;i < nodeList.size(); i++){
                 Expression ex = nodeList.get(i);
                 if(ex.isVariableDeclarationExpr()){
                     VariableDeclarationExpr varDeclExpr = ex.asVariableDeclarationExpr();
                     NodeList<VariableDeclarator> varList = varDeclExpr.getVariables();
                     for(int j=0;j < varList.size();j++){
                         VariableDeclarator varDecl = varList.get(j);
                         if(varDecl.getType().asString().equals("int")){
                             Expression ex2 = varDecl.getInitializer().get();
                             //System.out.println(varDecl.toString()+"  "+varDecl.getType().asString());
                             BinaryExpr binaryExpr = new BinaryExpr(varDecl.getInitializer().get(),new IntegerLiteralExpr(1), BinaryExpr.Operator.PLUS);
                             varDecl.setInitializer(binaryExpr);
                             outputToFile(f, fs);
                         }
                     }
                 }
             }
         }catch (Exception e){
         }
     }

     private void minusMutantGen(ForStmt f) {
         try{
             ForStmt fs =  f.clone();
             BinaryExpr be =(BinaryExpr)fs.getCompare().get();
             if (be == null) {
                 return;
             }
             BinaryExpr beTemp = new BinaryExpr(be.getRight(), new IntegerLiteralExpr("1"),BinaryExpr.Operator.MINUS);
             be.setRight(beTemp);
             outputToFile(f, fs);
         }
         catch (NoSuchElementException e){
             // no compare operator
         }
     }

     private void assignmentMutantGen(ForStmt f) {
         try{
             ForStmt fs2 = f.clone();
             BinaryExpr be2 = (BinaryExpr) fs2.getCompare().get();
             if (be2 == null) {
                 return;
             }
             AssignExpr ae = new AssignExpr(be2.getLeft(), be2.getRight(),AssignExpr.Operator.ASSIGN);
             fs2.setUpdate(new NodeList(ae));
             outputToFile(f, fs2);
         }
         catch (NoSuchElementException e){
             // no compare operator
             return;
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
