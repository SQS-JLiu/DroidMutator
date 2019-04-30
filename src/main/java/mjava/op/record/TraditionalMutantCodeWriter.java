package mjava.op.record;

import edu.ecnu.sqslab.mjava.MutationSystem;

import java.io.PrintWriter;

/**
 * Created by user on 2018/5/5.
 */
public class TraditionalMutantCodeWriter extends MutantCodeWriter {
    String method_signature = null;

    public TraditionalMutantCodeWriter( PrintWriter out ) {
        super(out);
    }

    public TraditionalMutantCodeWriter( String mutant_dir, PrintWriter out ) {
        super(mutant_dir,out);
    }

    public void setMethodSignature(String str){
        method_signature = str;
    }

    protected void writeLog(String changed_content) {
        CodeChangeLog.writeLog(class_name+ MutationSystem.LOG_IDENTIFIER
                + mutated_start +MutationSystem.LOG_IDENTIFIER
                + method_signature + MutationSystem.LOG_IDENTIFIER+" "
                +changed_content);
    }
}
