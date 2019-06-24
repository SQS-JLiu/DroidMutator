package mjava.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to execute instructions in SHELL and return output results
 */
public class ShellUtil {
    private static boolean isWindows = isWindowsSystem();

    /**
     * Execute the command directly and return the result
     * 
     * @return map {"exitFlag": 0 means normal end, others mean abnormal ,"output": output result}
     */
    public static Map<String, String> execute(String cmd, Map<String, String> envParams) {
        List<String> cmdList = new ArrayList<String>();
        if(!isWindows){
            //Linux use shell
            cmdList.add("sh");
            cmdList.add("-c");
        }else{
            //Windows use CMD
            cmdList.add("cmd");
            cmdList.add("/c");
        }
        cmdList.add(cmd);
        StringBuffer rsltStrBuffer = new StringBuffer(); // Save the returned result information
        int exitFlag = -1; // Wait for the SHELL thread to complete the identity
        Process proc = null;
        try {
            // System.out.println("Start Command :"+cmdList.toString());
            // Start command
            ProcessBuilder pb = new ProcessBuilder(cmdList);
            if (envParams != null) {
                pb.environment().putAll(envParams);
            }

            pb.redirectErrorStream(true);
            proc = pb.start();
            // Read command return information and error information
            readNormalAndErrorInfo(rsltStrBuffer, proc);

            // Wait for the command to complete and return to the command execution state
            exitFlag = proc.waitFor();
        }
        catch (Exception e) {
            System.out.println("Command [" + cmdList + "] Execute  Error: " + e.toString());
        }
        finally {
            if (proc != null)
                proc.destroy();
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("exitFlag", String.valueOf(exitFlag));
        result.put("output", rsltStrBuffer.toString());
        return result;
    }

    private static void readNormalAndErrorInfo(StringBuffer rsltStrBuffer, Process proc) {
        try {
            int c;
            StringBuilder line = new StringBuilder();
            while ((c = proc.getInputStream().read()) != -1) {
                rsltStrBuffer.append((char) c);
                if (c == 10 || c == 13) {
                    String logStr = line.toString();
                    // Remove empty lines
                    if (logStr.length() > 0) {
                        System.out.println(logStr);
                        //BuildOutputPane.getInstance().writePane(logStr+"\n");
                        line.delete(0, line.length());
                    }
                }
                else {
                    line.append((char) c);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public static boolean isWindowsSystem(){
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
    
    public static void main(String[] args) {
        //testing code
        System.out.println("----System type:------"+isWindowsSystem());
        System.out.println(System.getProperty("os.name").toUpperCase());
        //Map<String, String>  result = ShellUtil.execute("D:\\Python27\\python ./builder/compileAndroidPro.py",null);
        //System.out.println(result.toString());
    }
}