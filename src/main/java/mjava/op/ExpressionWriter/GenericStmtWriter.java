package mjava.op.ExpressionWriter;

import com.github.javaparser.ast.stmt.Statement;
import mjava.op.record.TraditionalMutantCodeWriter;
import mjava.op.record.WriteJavaFile;

import java.io.File;
import java.io.PrintWriter;

public class GenericStmtWriter extends TraditionalMutantCodeWriter {
    Statement original;
    Statement mutant;
    public GenericStmtWriter(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     */
    public void setMutant(Statement exp1, Statement exp2) {
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
        //System.out.println("start: "+mutated_start);
        String log_str =original.toString()+ "  =>  " +mutant.toString();
        writeLog(removeNewline(log_str));
    }
}
