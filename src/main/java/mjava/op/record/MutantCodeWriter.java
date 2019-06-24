package mjava.op.record;

import edu.ecnu.sqslab.mjava.MutationSystem;

import java.io.PrintWriter;

/**
 * Used to output the mutation log
 * Created by user on 2018/5/5.
 */
public class MutantCodeWriter {
    protected PrintWriter out;
    public String class_name = null;
    public int line_num = 1;
    public int mutated_start = -1;

    public MutantCodeWriter(PrintWriter out ) {
        this.out = out;
    }

    public MutantCodeWriter(String mutant_dir, PrintWriter out ) {
        this.out = out;
        class_name = mutant_dir;
    }

    public void setClassName(String str) {
        class_name = str;
    }

    protected String removeNewline(String str){
        int index;
        while((index = str.indexOf("\n"))>=0){
            if(index>0 && index<str.length()){
                str = str.substring(0,index-1)+str.substring(index+1,str.length());
            }else if(index==0){
                str = str.substring(1,str.length());
            }else if(index==str.length()){
                str = str.substring(0,index-1);
            }
        }
        return str;
    }

    protected void writeLog(String changed_content)
    {
        CodeChangeLog.writeLog(class_name+ MutationSystem.LOG_IDENTIFIER
                +mutated_start+MutationSystem.LOG_IDENTIFIER+changed_content);
    }
}
