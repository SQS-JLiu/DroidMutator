package mjava.op.new_op;

import com.github.javaparser.ast.expr.ConditionalExpr;
import mjava.op.record.TraditionalMutantCodeWriter;
import mjava.op.record.WriteJavaFile;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by user on 2018/5/7.
 * @author  Jian Liu
 */
public class CER_Writer extends TraditionalMutantCodeWriter {
    ConditionalExpr original;
    ConditionalExpr mutant;

    public CER_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setMutant(ConditionalExpr exp1, ConditionalExpr exp2) {
        original = exp1;
        mutant = exp2;
    }

    /**
     * Log mutated line
     */
    public void writeFile( File original_file )
    {
        new WriteJavaFile(original_file,out).writeFile(original,mutant);
        mutated_start = line_num =original.getBegin().get().line;
        String log_str =original.toString()+ "  =>  " +mutant.toString();
        writeLog(removeNewline(log_str));
    }
}
