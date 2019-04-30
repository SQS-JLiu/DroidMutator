package mjava.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellUtil {
    private static boolean isWindows = isWindowsSystem();

    /**
     * Execute the command directly and return the result
     * 
     * @return map {"exitFlag": 0表示正常结束，其他表示异常 ,"output": 窗口输出结果}
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
        StringBuffer rsltStrBuffer = new StringBuffer(); // 保存返回的结果信息
        int exitFlag = -1; // 等待SHELL线程完成的标识
        Process proc = null;
        try {
            // System.out.println("Start Command :"+cmdList.toString());
            // 启动命令
            ProcessBuilder pb = new ProcessBuilder(cmdList);
            if (envParams != null) {
                pb.environment().putAll(envParams);
            }

            pb.redirectErrorStream(true);
            proc = pb.start();
            // 读取命令返回信息和错误信息
            readNormalAndErrorInfo(rsltStrBuffer, proc);

            // 等待命令执行完成，并返回命令执行状态
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

    private static void readNormalAndErrorInfo(StringBuffer rsltStrBuffer, Process proc) throws IOException {
        try {
            int c;
            StringBuilder line = new StringBuilder();
            while ((c = proc.getInputStream().read()) != -1) {
                rsltStrBuffer.append((char) c);
                if (c == 10 || c == 13) {
                    String logStr = line.toString();
                    // 去掉空行
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