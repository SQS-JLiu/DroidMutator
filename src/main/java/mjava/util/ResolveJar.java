package mjava.util;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResolveJar {
    public static final String JAR_DIR_NAME = "apk2jar";

    /**
     * parse apk into dex
     * @param apkPath
     * @param outputDir
     */
    static public void zipDecompressing(String apkPath, String outputDir){
        System.out.println("Starting decompress apk file ......");
        long startTime=System.currentTimeMillis();
        try {
            ZipInputStream Zin=new ZipInputStream(new FileInputStream(apkPath));//Input source zip path
            BufferedInputStream Bin=new BufferedInputStream(Zin);
            ZipEntry entry;
            try {
                while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){
                    if(!entry.getName().endsWith(".dex")){
                        continue;
                    }
                    File Fout=new File(outputDir,entry.getName());
                    if(!Fout.exists()){
                        (new File(Fout.getParent())).mkdirs();
                    }
                    FileOutputStream out=new FileOutputStream(Fout);
                    BufferedOutputStream Bout=new BufferedOutputStream(out);
                    int b;
                    while((b=Bin.read())!=-1){
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    System.out.println(Fout+" decompressing succeeded.");
                }
                Bin.close();
                Zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long endTime=System.currentTimeMillis();
        System.out.println("Apk decompression takes time： "+(endTime-startTime)+" ms.");
    }

    /**
     * parse dex into jar
     * @param inputDir
     */
    static public void dexDecompressing(String inputDir){
        System.out.println("Starting decompress dex file ......");
        long startTime=System.currentTimeMillis();
        File inputFileDir = new File(inputDir);
        if(!inputFileDir.exists()){
            return;
        }
        String[] paramArr = new String[6];
        paramArr[0] = "-f";
        for(File f : inputFileDir.listFiles()){
            if(f.getName().endsWith(".dex")){
                paramArr[1] = "-o";
                paramArr[2] = inputDir+"\\"+f.getName().replace(".dex","")+"-dex2jar.jar";
                paramArr[3] = "-e";
                paramArr[4] = inputDir+"\\"+f.getName().replace(".dex","")+"-error.zip";
                paramArr[5] = f.getAbsolutePath();
                //com.googlecode.dex2jar.tools.Dex2jarCmd.main(paramArr);
                //System.out.println("absPath: "+f.getAbsolutePath());
            }
        }
        long endTime=System.currentTimeMillis();
        System.out.println("Dex decompression takes time： "+(endTime-startTime)+" ms.");
    }

    /**
     * add the class path dynamically
     *
     */
    private static void addURL(String classPath) {
        try{
            Method addClass = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            addClass.setAccessible(true);
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            // load app's dependence jars
            File[] jarFiles = new File(classPath).listFiles();
            for (File ff : jarFiles) {
                if(ff.getName().endsWith(".jar")){
                    addClass.invoke(cl, new Object[]{ff.toURL()});
                    System.out.println("FileName: "+ff.getName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }

    /**
     * parse apk into jar
     * @param apkPath
     */
    public static void doApk2Jar(String apkPath){
        if("".equals(apkPath)) return;
        File path = new File(apkPath);
        if(path.exists() && path.isDirectory()){
            boolean flag = false;
            for(File f: path.listFiles()){
                if(f.getName().endsWith(".apk")){
                    flag = true;
                    File apk2jarDir = new File(apkPath+File.separator+JAR_DIR_NAME);
                    if(!apk2jarDir.exists()){
                        apk2jarDir.mkdirs();
                    }
                    zipDecompressing(f.getAbsolutePath(),apk2jarDir.getAbsolutePath());
                    dexDecompressing(apk2jarDir.getAbsolutePath());
                }
            }
            if(!flag){
                System.err.println("Can not find apk file. Parsing apk into jar failed.");
            }
        }else if(path.isFile()){
            File apk2jarDir = new File(path.getParent()+File.separator+JAR_DIR_NAME);
            if(!apk2jarDir.exists()){
                apk2jarDir.mkdirs();
            }
            zipDecompressing(path.getAbsolutePath(),apk2jarDir.getAbsolutePath());
            dexDecompressing(apk2jarDir.getAbsolutePath());
        }
        else{
            System.err.println("Can not find path ["+apkPath+"]. parsing apk into jar failed.");
        }
    }


    public static void main(String[] args){
        //my testing
        //zipDecompressing("E:\\AndroidStudioProjects\\MyApplication\\app\\build\\outputs\\apk\\app-debug.apk","E:\\mutatorHome\\classes");
        //zipDecompressing("E:\\andriod_project\\AnyMemo\\app\\build\\outputs\\apk\\debug\\AnyMemo-debug.apk","E:\\mutatorHome\\classes");
        //dexDecompressing("E:\\mutatorHome\\classes");
        addURL("E:\\mutatorHome\\classes");
        String[] className = new String[]{"org.liberty.android.fantastischmemo.ui.CardEditor",
                "org.apache.commons.io.ByteOrderMark",
                "android.support.v7.app.AppCompatActivity",
                "org.jacoco.core.data.SessionInfo"};
        try{
            for(String name: className){
                Class c = Class.forName(name);
                System.out.println("ClassName: "+c.getName()+ " superClass:"+c.getSuperclass().getName());
            }
        }catch (ClassNotFoundException cnfe){
            System.err.println(cnfe.toString());
        }
    }
}
