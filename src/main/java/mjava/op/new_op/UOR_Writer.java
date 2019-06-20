package mjava.op.new_op;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import mjava.op.record.TraditionalMutantCodeWriter;
import mjava.op.record.WriteJavaFile;

import java.io.File;
import java.io.PrintWriter;

public class UOR_Writer extends TraditionalMutantCodeWriter {
    UnaryExpr original;
    AssignExpr mutant;

    public UOR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setMutant(UnaryExpr exp1, AssignExpr exp2) {
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
