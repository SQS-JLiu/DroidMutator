package mjava.op.record;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/5/6.
 * Used to output mutant data and generate mutant files
 * @author Jian Liu
 */
public class WriteJavaFile {
    private CompilationUnit comp_unit;
    private PrintWriter out;
    private ArrayList<String> codeLines;

    public WriteJavaFile() {
    }

    public WriteJavaFile(File original_file, PrintWriter out) {
        try {
            comp_unit = JavaParser.parse(new java.io.FileInputStream(original_file));
            codeLines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(original_file));
            String tempString;
            codeLines.add(""); // The number of lines starts from 1.
            while ((tempString = br.readLine()) != null){
                codeLines.add(tempString);
            }
            br.close();
        } catch (java.io.FileNotFoundException e) {
            comp_unit = null;
            System.err.println("File " + original_file + " not found." + e);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        this.out = out;
    }

    /**
     * output mutant string
     * @param codeLines
     * @param original
     * @param mutant
     * @return
     */
    public String printStringList(ArrayList<String> codeLines,Node original,Node mutant) {
        if(!original.getBegin().isPresent() || !original.getEnd().isPresent()){
            return  comp_unit.toString();
        }
        int startLine = original.getBegin().get().line;
        int endLine = original.getEnd().get().line;
        int startColumn = original.getBegin().get().column;
        int endColumn = original.getEnd().get().column;
        StringBuilder stub = new StringBuilder();
        for (int line=1;line<startLine;line++){
            stub.append(codeLines.get(line)+ System.getProperty("line.separator"));
        }
        try{
            String tmpBefore = codeLines.get(startLine).substring(0,startColumn-1);
            String tmpAfter ="";
            if(codeLines.get(endLine).length() > endColumn){
                tmpAfter = codeLines.get(endLine).substring(endColumn);
            }
            stub.append(tmpBefore + mutant.toString() + tmpAfter + System.getProperty("line.separator"));
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("Line: "+startLine+",Column:"+startColumn +"==>" + codeLines.get(startLine));
            System.err.println("Line: "+endLine+",Column:"+endColumn +"==>" + codeLines.get(endLine));
        }
        for(int line = endLine+1;line<codeLines.size();line++){
            stub.append(codeLines.get(line)+System.getProperty("line.separator"));
        }
        return stub.toString();
    }

    /**
     * @deprecated
     * @param codeLines
     * @param original
     * @param mutant
     * @return
     */
    public String printStringList_old(ArrayList<String> codeLines,Node original,Node mutant) {
        //原格式替换  Original format replacement
        LexicalPreservingPrinter.setup(original);
        //System.out.println(LexicalPreservingPrinter.print(original));
        String[] originals = LexicalPreservingPrinter.print(original).split(System.getProperty("line.separator"));
        String[] mutants = mutant.toString().split(System.getProperty("line.separator"));
        if(!original.getBegin().isPresent() || !original.getEnd().isPresent()){
            return comp_unit.toString();
        }
        int i = original.getBegin().get().line,j=0;
        for( ;i<= original.getEnd().get().line;i++,j++){
            if( j < mutants.length){
                codeLines.set(i-1,codeLines.get(i-1).replace(originals[j],mutants[j]));
            }else {
                codeLines.set(i-1,codeLines.get(i-1).replace(originals[j],""));
            }
            //System.out.println(codeLines.get(i-1)+"|"+originals[j]+"|"+mutants[j]);
        }
        while(j < mutants.length){
            codeLines.add(i,mutants[j]);
            j++;i++;
        }
        StringBuilder sb = new StringBuilder();
        for (String line : codeLines) {
            sb.append(line + System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void setComp_unit(CompilationUnit comp_unit) {
        this.comp_unit = comp_unit;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public boolean writeFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<MethodCallExpr> meList = comp_unit.getNodesByType(MethodCallExpr.class);
        for (MethodCallExpr mecall : meList) {
            if (mecall.equals(original) && isEqualLine(mecall, original)) {
                //mecall.replace(mutant);
                //LexicalPreservingPrinter.setup(comp_unit);
                //out.println(LexicalPreservingPrinter.print(comp_unit));
                //out.println(comp_unit.toString());
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(ConditionalExpr original, ConditionalExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<ConditionalExpr> meList = comp_unit.getNodesByType(ConditionalExpr.class);
        for (ConditionalExpr conditionalExpr : meList) {
            if (conditionalExpr.equals(original) && isEqualLine(conditionalExpr, original)) {
                //conditionalExpr.replace(mutant);
                //out.println(comp_unit.toString());
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(ForStmt original, ForStmt mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<ForStmt> meList = comp_unit.getNodesByType(ForStmt.class);
        for (ForStmt forStmt : meList) {
            if (forStmt.equals(original) && isEqualLine(forStmt, original)) {
                //forStmt.replace(mutant);
                //out.println(comp_unit.toString());
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(BinaryExpr original, BinaryExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<BinaryExpr> meList = comp_unit.getNodesByType(BinaryExpr.class);
        for (BinaryExpr binaryExpr : meList) {
            if (binaryExpr.equals(original) && isEqualLine(binaryExpr, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    // generic expression writer
    public boolean writeFile(Expression original, Expression mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<Expression> meList = comp_unit.getNodesByType(Expression.class);
        for (Expression expr : meList) {
            if (expr.equals(original) && isEqualLine(expr, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    // generic node writer
    public boolean writeFile(Node original, Node mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<Node> meList = comp_unit.getNodesByType(Node.class);
        for (Node node : meList) {
            if (node.equals(original) && isEqualLine(node, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(MethodCallExpr original, NameExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<MethodCallExpr> meList = comp_unit.getNodesByType(MethodCallExpr.class);
        for (MethodCallExpr mecall : meList) {
            if (mecall.equals(original) && isEqualLine(mecall, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(ObjectCreationExpr original, NameExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<ObjectCreationExpr> meList = comp_unit.getNodesByType(ObjectCreationExpr.class);
        for (ObjectCreationExpr mecall : meList) {
            if (mecall.equals(original) && isEqualLine(mecall, original)) {
                if (original.getParentNode().get().toString().startsWith(mecall.toString())) {
                    //mecall.replace(mutant);
                    out.println(printStringList(codeLines,original,mutant));
                } else {
                    //mecall.getParentNode().get().replace(mutant);
                    out.println(printStringList(codeLines,original.getParentNode().get(),mutant));
                }
                //out.println(comp_unit.toString());
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(MethodCallExpr original, BlockStmt mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<MethodCallExpr> meList = comp_unit.getNodesByType(MethodCallExpr.class);
        for (MethodCallExpr mecall : meList) {
            if (mecall.equals(original) && isEqualLine(mecall, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(UnaryExpr original, Expression mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<UnaryExpr> meList = comp_unit.getNodesByType(UnaryExpr.class);
        for (UnaryExpr expr : meList) {
            if (expr.equals(original) && isEqualLine(expr, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(AssignExpr original, AssignExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<AssignExpr> meList = comp_unit.getNodesByType(AssignExpr.class);
        for (AssignExpr expr : meList) {
            if (expr.equals(original) && isEqualLine(expr, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean writeFile(VariableDeclarationExpr original, VariableDeclarationExpr mutant) {
        if (comp_unit == null) {
            return false;
        }
        List<VariableDeclarationExpr> meList = comp_unit.getNodesByType(VariableDeclarationExpr.class);
        for (VariableDeclarationExpr expr : meList) {
            if (expr.equals(original) && isEqualLine(expr, original)) {
                out.println(printStringList(codeLines,original,mutant));
                return true;
            }
        }
        out.println(comp_unit.toString());
        return false;
    }

    public boolean isEqualLine(Node node1, Node node2) {
        if (node1.getBegin().get().line == node2.getBegin().get().line) {
            return true;
        }
        return false;
    }
}
