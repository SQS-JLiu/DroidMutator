package mjava.op.operator;

import com.github.javaparser.ast.stmt.ForStmt;
import mjava.op.record.TraditionalMutantCodeWriter;
import mjava.op.record.WriteJavaFile;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by user on 2018/5/7.
 */
public class FLR_Writer extends TraditionalMutantCodeWriter {
    ForStmt original;
    ForStmt mutant;

    public FLR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setMutant(ForStmt exp1, ForStmt exp2) {
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
