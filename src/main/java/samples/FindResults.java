package samples;

import java.io.File;

public class FindResults {
    public  static int count = 0;

    public static void getFailedMutantDir(File resultFileDir){
        for(File f : resultFileDir.listFiles()){
            if(f.isDirectory()){
                getFailedMutantDir(f);
            }else {
                if(f.getName().endsWith(".java")){
                    File ff = new File(f.getParent()+File.separator+"app-debug.apk");
                    if(!ff.exists()){
                        System.out.println(f.getParent());
                        count++;
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        //ASR  The type of local variable may not be detected
        //File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\blokish");
        //File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\ParkenDD");

        //ViewComponentNotVisible  solved
        //File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\CamTimer");
        //File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\Shaarlier");

        //File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\tuner");
        File resultFileDir = new File("E:\\mutatorHome\\experiments\\apk_output\\Primary");
        if(!resultFileDir.exists()){
            System.out.println("dir not exists.");
        }
        getFailedMutantDir(resultFileDir);
        System.out.println("Count: "+count);
    }
}
