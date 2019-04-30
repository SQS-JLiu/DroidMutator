package mjava.op.record;

import edu.ecnu.sqslab.mjava.MutationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/5/14.
 * @author  Jian Liu
 */
public class MultiThreadTriggerLog {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadTriggerLog.class);
    public static final String ThreadType = "Thread_Trigger.txt";
    public static final String AsyncTaskType = "AsyncTask_Trigger.txt";
    public static final String HandlerType = "Handler_Trigger.txt";
    public static final String RunOnUIThreadType = "RunOnUiThread_Trigger.txt";
    private static PrintWriter thread_writer;
    private static PrintWriter asyncTask_writer;
    private static PrintWriter handler_writer;
    private static PrintWriter runOnUiThread_writer;
    private static List<String> threadList = new ArrayList<String>();
    private static List<String> asyncTaskList = new ArrayList<String>();
    private static List<String> handlerList = new ArrayList<String>();
    private static List<String> runOnUiThreadList = new ArrayList<String>();
    private static Type type = null;
    public static enum Type{
        THREAD,
        ASYNCTASK,
        HANDLERTYPE,
        RUNONUITHREAD
    }

    public static void setType(Type t){
        type = t;
    }

    public static Type getType(){
        return type;
    }

    public static boolean clearList(){
        threadList.clear();
        asyncTaskList.clear();
        handlerList.clear();
        runOnUiThreadList.clear();
        return true;
    }

    public static boolean writeAllLog(String className){
        if("".equals(className)){
            System.err.println("MultiThreadTriggerLog : Writing thread type,but class name is empty.");
            return false;
        }
        if(threadList.size() > 0){
            thread_writer.println(className+":");
            for(int i=0;i < threadList.size();i++){
                thread_writer.println(threadList.get(i));
            }
            thread_writer.println();
        }
        if(asyncTaskList.size() > 0){
            asyncTask_writer.println(className+":");
            for(int i=0;i < asyncTaskList.size();i++){
                asyncTask_writer.println(asyncTaskList.get(i));
            }
            asyncTask_writer.println();
        }
        if(handlerList.size() > 0){
            handler_writer.println(className+":");
            for(int i=0;i < handlerList.size();i++){
                handler_writer.println(handlerList.get(i));
            }
            handler_writer.println();
        }
        if(runOnUiThreadList.size() > 0){
            runOnUiThread_writer.println(className+":");
            for(int i=0;i < runOnUiThreadList.size();i++){
                runOnUiThread_writer.println(runOnUiThreadList.get(i));
            }
            runOnUiThread_writer.println();
        }
        return true;
    }

    public static void openLogFile(){
        try{
            File t = new File(MutationSystem.MUTANT_HOME,ThreadType);
            FileWriter t_out = new FileWriter(t);
            thread_writer = new PrintWriter(t_out);

            File a = new File(MutationSystem.MUTANT_HOME,AsyncTaskType);
            FileWriter a_out = new FileWriter(a);
            asyncTask_writer = new PrintWriter(a_out);

            File h = new File(MutationSystem.MUTANT_HOME,HandlerType);
            FileWriter h_out = new FileWriter(h);
            handler_writer = new PrintWriter(h_out);

            File r = new File(MutationSystem.MUTANT_HOME,RunOnUIThreadType);
            FileWriter r_out = new FileWriter(r);
            runOnUiThread_writer = new PrintWriter(r_out);
        }catch(IOException e){
            System.err.println("[IOException] Can't make mutant log file." + e);
            logger.error("[IOException] Can't make mutant log file." + e);
        }
    }

    public static void writeList(String str){
        if(type == null){
            System.err.println("MultiThreadTriggerLog : Writing thread type log, but type is null.");
            return;
        }
        if(type == Type.THREAD){
            threadList.add(str);
        }
        else if(type == Type.ASYNCTASK){
            asyncTaskList.add(str);
        }
        else if(type == Type.HANDLERTYPE){
            handlerList.add(str);
        }
        else if(type == Type.RUNONUITHREAD){
            runOnUiThreadList.add(str);
        }
    }

    public static void write2ThreadList(String str){
        threadList.add(str);
    }

    public static void write2AsyncTaskList(String str){
        asyncTaskList.add(str);
    }

    public static void write2handlerlist(String str){
        handlerList.add(str);
    }

    public static void write2RunOnUiThreadList(String str){
        runOnUiThreadList.add(str);
    }

    public static void writeThread(String str){
        thread_writer.println(str);
    }
    public static void writeAsyncTask(String str){
        asyncTask_writer.println(str);
    }
    public static void writeHandler(String str){
        handler_writer.println(str);
    }
    public static void writeRunOnUiThread(String str){
        runOnUiThread_writer.println(str);
    }

    public static void closeLogFile(){
        thread_writer.flush();
        thread_writer.close();
        asyncTask_writer.flush();
        asyncTask_writer.close();
        handler_writer.flush();
        handler_writer.close();
        runOnUiThread_writer.flush();
        runOnUiThread_writer.close();
    }

    public static boolean clearLogFile(){
        File file = new File(MutationSystem.MUTANT_HOME);
        File[] files = file.listFiles();
        if (files == null){
            return true;
        }
        for(File f : files){
            if(ThreadType.equals(f.getName()) || AsyncTaskType.equals(f.getName())||
                    HandlerType.equals(f.getName())|| RunOnUIThreadType.equals(f.getName())){
                f.delete();
            }
        }
        return true;
    }
}
