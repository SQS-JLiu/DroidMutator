package mjava.op.operator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.record.MethodLevelMutator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Array value substitution; e.g., int[] a; method(a); ==> a[0]=0; method(a)  /  a[a.length/2]=0; method(a)
 *                                                    /  a[a.length-1]=0;  method(a);
 */
 /**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class AVR extends MethodLevelMutator {

    public AVR(CompilationUnit comp_unit,File originalFile) {
        super(comp_unit,originalFile);
    }

     protected  void generateMutants(MethodDeclaration p){
        //1.MethodCallExpr 方法调用表达式，入参为数组引用时
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit(MethodCallExpr m, Object obj){
                 super.visit(m,obj);
                 if(skipMutation(m)){
                     return;
                 }
                 NodeList<Expression> expList = m.getArguments();
                 for (int i = 0; i < expList.size(); i++) {
                     Expression e = expList.get(i);
                     try{
                         if (e.isNameExpr()) {
                             //int short long float double
                             ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(e);
                             //System.out.println("Variable: "+ e.toString()+ ", Test type: "+t.describe());
                             if(!t.isArray()){
                                 continue;
                             }
                             if(t.describe().equals("int") || t.describe().equals("int[]")||t.describe().equals("java.lang.Long")
                                    || t.describe().equals("long") || t.describe().equals("long[]") ){
                                 insertMutant(m,e,0);
                                 insertMutant2(m,e,0);
                                 insertMutant3(m,e,0);
                             }else if(t.describe().equals("char")||t.describe().equals("char[]")) { //char
                                 insertMutant(m,e,1);
                                 insertMutant2(m,e,1);
                                 insertMutant3(m,e,1);
                             }
                         }
                     } catch (Exception uex){
                         uex.printStackTrace();
                          System.err.println("AVR: "+e.toString()+" ,Unsolved Symbol Exception, ignore...");
                     }
                 }
             }
         },null);
         //2.ObjectCreationExpr  对象创建表达式，入参为数组引用时
         p.accept(new VoidVisitorAdapter<Object>() {
             @Override
             public void visit( ObjectCreationExpr m, Object obj) {
                 super.visit(m, obj);
                 if(skipMutation(m)){
                     return;
                 }
                 NodeList<Expression> expList = m.getArguments();
                 for (int i = 0; i < expList.size(); i++) {
                     Expression e = expList.get(i);
                     try{
                         if (e.isNameExpr()) {
                             ResolvedType t = e.calculateResolvedType();
                             if(!t.isArray()){
                                 continue;
                             }
                             //System.out.println("*************Test : "+m.getParentNode().get().toString());
                             //System.out.println("*************Test2 : "+m.toString());
                             //int short long float double
                             if(t.describe().equals("int") || t.describe().equals("int[]")||t.describe().equals("java.lang.Long")
                                     || t.describe().equals("long") || t.describe().equals("long[]")){
                                 insertMutant(m,e,0);
                                 insertMutant2(m,e,0);
                                 insertMutant3(m,e,0);
                             }else if(t.describe().equals("char")||t.describe().equals("char[]")) { //char
                                 insertMutant(m,e,1);
                                 insertMutant2(m,e,1);
                                 insertMutant3(m,e,1);
                             }
                         }
                     } catch (Exception uex){
                     // System.err.println(e.toString()+" ,Unsolved Symbol Exception, ignore...");
                    }
                 }
             }
         },null);
     }
     //1.MethodCallExpr
     private void insertMutant(MethodCallExpr m, Expression exp, int flag) {
        //变异数组第一个值为0
        ArrayAccessExpr aaExpr = new ArrayAccessExpr();
        aaExpr.setName(exp);
        aaExpr.setIndex(new IntegerLiteralExpr(0));
        AssignExpr assignExpr;
        if(flag == 0){
            assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
        }else {
            assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
        }
        NameExpr nameExpr = new NameExpr();
        nameExpr.setName(assignExpr.toString()+";" + m.toString());
        outputToFile(m, nameExpr);
     }

     //2.MethodCallExpr
     private void insertMutant2(MethodCallExpr m, Expression exp, int flag){
         //变异数组中间的值
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length - 1"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+";" + m.toString());
         outputToFile(m, nameExpr);
     }

     //3.MethodCallExpr
     private void insertMutant3(MethodCallExpr m, Expression exp, int flag){
         //变异数组最后一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length / 2"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+";" + m.toString());
         outputToFile(m, nameExpr);
     }

     //1.ObjectCreationExpr
     private void insertMutant(ObjectCreationExpr m, Expression exp, int flag) {
         //变异数组第一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new IntegerLiteralExpr(0));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; " + m.getParentNode().get().toString());
         outputToFile(m, nameExpr);
     }

     //2.ObjectCreationExpr
     private void insertMutant2(ObjectCreationExpr m, Expression exp, int flag){
         //变异数组中间的值
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length - 1"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; " + m.getParentNode().get().toString());
         outputToFile(m, nameExpr);
     }

     //3.ObjectCreationExpr
     private void insertMutant3(ObjectCreationExpr m, Expression exp, int flag){
         //变异数组最后一个值为0
         ArrayAccessExpr aaExpr = new ArrayAccessExpr();
         aaExpr.setName(exp);
         aaExpr.setIndex(new NameExpr(exp.toString()+".length / 2"));
         AssignExpr assignExpr;
         if(flag == 0){
             assignExpr = new AssignExpr(aaExpr,new IntegerLiteralExpr(0),AssignExpr.Operator.ASSIGN);
         }else {
             assignExpr = new AssignExpr(aaExpr,new CharLiteralExpr('0'),AssignExpr.Operator.ASSIGN);
         }
         NameExpr nameExpr = new NameExpr();
         nameExpr.setName(assignExpr.toString()+"; " + m.getParentNode().get().toString());
         outputToFile(m, nameExpr);
     }


    /**
     * Output CAR mutants to files (MethodCallExpr)
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("AVR");
        String mutant_dir = getMuantID("AVR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            AVR_Writer writer = new AVR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("AVR: Fails to create " + f_name);
        }
    }

     /**
      * Output CAR mutants to files (ObjectCreationExpr)
      *
      * @param original
      * @param mutant
      */
     public void outputToFile(ObjectCreationExpr original, NameExpr mutant) {
         if (comp_unit == null || currentMethodSignature == null){
             return;
         }
         num++;
         String f_name = getSourceName("AVR");
         String mutant_dir = getMuantID("AVR");
         try {
             PrintWriter out = getPrintWriter(f_name);
             AVR_Writer writer = new AVR_Writer(mutant_dir, out);
             writer.setMutant(original, mutant);
             writer.setMethodSignature(currentMethodSignature);
             writer.writeFile2(original_file);
             out.flush();
             out.close();
         }
         catch (IOException e) {
             System.err.println("AVR: Fails to create " + f_name);
         }
     }
}
