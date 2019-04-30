package mjava.op.operator;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import mjava.op.record.TraditionalMutantCodeWriter;
import mjava.op.record.WriteJavaFile;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class AVR_Writer extends TraditionalMutantCodeWriter {
    MethodCallExpr original;
    NameExpr mutant;
    ObjectCreationExpr originalObj;
    NameExpr mutantObj;

    public AVR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     */
    public void setMutant(MethodCallExpr exp1, NameExpr exp2) {
        original = exp1;
        mutant = exp2;
    }

    public void setMutant(ObjectCreationExpr exp1, NameExpr exp2) {
        originalObj = exp1;
        mutantObj = exp2;
    }

    /**
     * Log mutated line
     */
    public void writeFile(File original_file )
    {
        new WriteJavaFile(original_file,out).writeFile(original,mutant);
        mutated_start = line_num =original.getBegin().get().line;
        String log_str =original.toString()+ "  =>  " +mutant.toString();
        writeLog(removeNewline(log_str));
    }

    /**
     * Log mutated line
     */
    public void writeFile2( File original_file )
    {
        new WriteJavaFile(original_file,out).writeFile(originalObj,mutantObj);
        mutated_start = line_num =originalObj.getBegin().get().line;
        String log_str =originalObj.toString()+ "  =>  " +mutantObj.toString();
        writeLog(removeNewline(log_str));
    }
}
