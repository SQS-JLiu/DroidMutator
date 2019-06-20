package edu.ecnu.sqslab.mjava;

import mjava.gui.main.GenMutantsMain;
import mjava.model.ImplementINFO;
import mjava.model.InheritanceINFO;
import mjava.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Vector;

/**
 * Created by user on 2018/5/5.
 */
public class MutationSystem {
    private static final Logger logger = LoggerFactory.getLogger(MutationSystem.class);
    public static final int INTERFACE = 0;
    public static final int ABSTRACT = 1;
    public static final int GUI = 2;
    public static final int MAIN = 3;
    public static final int MAIN_ONLY = 4;
    public static final int NORMAL = 5;
    public static final int APPLET = 6;

    public static final int TM = 1; // Traditional Mutation Operator
    public static final int EM = 2; // Exceptional Mutation Operator


    /**
     * home path where inputs and output of mjava system are located
     */
    public static String SYSTEM_HOME = System.getProperty("user.dir");

    /**
     * path of Java source files which mutation is applied to
     */
    public static String SRC_PATH = SYSTEM_HOME + File.separator + "src";

    /**
     * path of classes of Java source files at CLASS_PATH directory
     */
    public static String CLASS_PATH = SYSTEM_HOME + File.separator + "classes";

    /**
     * path of dependence libs of Java source files at LIB_PATH directory
     */
    public static String LIB_PATH = SYSTEM_HOME + File.separator + "libs";

    /**
     * home path which mutants are put into
     */
    public static String MUTANT_HOME = SYSTEM_HOME + File.separator + "result";

    /**
     * path which traditional mutants are put into
     */
    public static String TRADITIONAL_MUTANT_PATH = "";

    /**
     * path which exception-related mutants are put into
     */
    public static String EXCEPTION_MUTANT_PATH = "";

    /**
     * ??? absolute path for ???
     */
    public static String MUTANT_PATH = "";

    /**
     * ??? absolute path for the original Java source
     */
    public static String ORIGINAL_PATH = "";

    /**
     * class name without package name that mutation is applied into
     */
    public static String CLASS_NAME;

    /**
     * path for
     */
    public static String DIR_NAME;

    /**
     * directory name for exception-related mutants
     */
    public static String EM_DIR_NAME = "exception_mutants";

    /**
     * directory name for traditional mutants
     */
    public static String TM_DIR_NAME = "mutants";

    /**
     * directory name for original class
     */
    public static String ORIGINAL_DIR_NAME = "original";

    public static String LOG_IDENTIFIER = ":";

    /**
     * List of names of traditional mutation operators
     */
    public static String[] tm_operators = {"ROR","AODs", "AODu", "AORb", "AORs", "AORu", "ASR", "LOR", "SOR",
            "---", "NullIntent", "NullLocation", "NullBackEndServiceReturn", "NullBluetoothAdapter", "InvalidURI",
            "InvalidIDFindView", "InvalidDate", "InvalidFilePath", "NullInputStream"};

    /*public static String[] tm_operators = {"IAR", "SAR", "CER", "AVR", "FLR", "UOR", "ROR"};*/

    /**
     * List of names of exception-related mutation operators
     */
    public static String[] em_operators = {"xxx"};

    public static String PYTHON_PATH = "python";

    public static String MUTATION_METHOD = "async";

    /**
     * Return type of class.
     *
     * @return type of class ( types: interface, abstract, GUI, main, normal, applet )
     */
    public static int getClassType(String class_name) {
        try {
            Class c = Class.forName(class_name);
            if (c.isInterface())
                return INTERFACE;

            if (Modifier.isAbstract(c.getModifiers()))
                return ABSTRACT;

            Method[] ms = c.getDeclaredMethods();

            if (ms != null) {
                if ((ms.length == 1) && (ms[0].getName().equals("main")))
                    return MAIN_ONLY;

                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().equals("main"))
                        return MAIN;
                }
            }

            if (isGUI(c)) return GUI;
            if (isApplet(c)) return APPLET;

            return NORMAL;
        } catch (Exception e) {
            return -1;
        } catch (Error e) {
            return -1;
        }
    }


    /**
     * Examine if class <i>c</i> is a GUI class
     */
    private static boolean isGUI(Class c) {
        while (c != null) {
            if ((c.getName().indexOf("java.awt") == 0) ||
                    (c.getName().indexOf("javax.swing") == 0))
                return true;

            c = c.getSuperclass();
            if (c.getName().indexOf("java.lang") == 0)
                return false;
        }
        return false;
    }


    /**
     * Examine if class <i>c</i> is an applet class
     */
    private static boolean isApplet(Class c) {
        while (c != null) {
            if (c.getName().indexOf("java.applet") == 0)
                return true;

            c = c.getSuperclass();
            if (c.getName().indexOf("java.lang") == 0)
                return false;
        }
        return false;
    }


    /**
     * Inheritance Informations
     */
    public static InheritanceINFO[] classInfo = null;


    /**
     * Clear arranged original file made before
     */
    private static void clearPreviousOriginalFiles() {
        File original_class_dir = new File(MutationSystem.ORIGINAL_PATH);
        int i;
        File[] old_files = original_class_dir.listFiles();

        if (old_files == null) return;

        for (i = 0; i < old_files.length; i++) {
            old_files[i].delete();
        }
    }


    /* Clear mutants generated from previous run (in class_mutants folder)*/
    static void clearPreviousMutants(String path) {
        File mutant_classes_dir = new File(path);
        int i;
        // delete previous mutant files
        File[] old_files = mutant_classes_dir.listFiles();

        if (old_files == null) return;

        for (i = 0; i < old_files.length; i++) {
            if (old_files[i].isDirectory()) {
                File[] old_mutants = old_files[i].listFiles();
                for (int j = 0; j < old_mutants.length; j++) {
                    old_mutants[j].delete();
                }
            }
            old_files[i].delete();
        }
    }


    /* Clear mutants generated from previous run (in traditional_mutants folder)*/
    static void clearPreviousTraditionalMutants(String path) {
        File traditional_mutant_dir = new File(path);
        int i;
        // delete previous mutant files
        File[] methods = traditional_mutant_dir.listFiles();

        if (methods == null)
            return;

        for (i = 0; i < methods.length; i++) {
            if (methods[i].isDirectory()) {
                File[] mutant_dir = methods[i].listFiles();

                for (int j = 0; j < mutant_dir.length; j++) {
                    if (mutant_dir[j].isDirectory()) {
                        File[] old_mutants = mutant_dir[j].listFiles();
                        for (int k = 0; k < old_mutants.length; k++) {
                            old_mutants[k].delete();
                        }
                    }
                    mutant_dir[j].delete();
                }
            }
            methods[i].delete();
        }
    }

    /**
     * Delete all traditional mutants generated before
     */
    public static void clearPreviousTraditionalMutants() {
        clearPreviousTraditionalMutants(MutationSystem.TRADITIONAL_MUTANT_PATH);
    }

    /**
     * Delete all mutants generated before
     */
    public static void clearPreviousMutants() {
        clearPreviousOriginalFiles();
        clearPreviousTraditionalMutants();
    }

    /* Set up target files (stored in src folder) to be tested */
    public static Vector getNewTragetFiles() {
        Vector targetFiles = new Vector();
        getJavacArgForDir(MutationSystem.SRC_PATH, "", targetFiles);
        return targetFiles;
    }

    protected static String getJavacArgForDir(String dir, String str, Vector targetFiles) {
        String result = str;
        String temp = "";

        File dirF = new File(dir);
        File[] javaF = dirF.listFiles(new ExtensionFilter("java"));
        if (javaF == null) {
            return null;
        }
        if (javaF.length > 0) {
            result = result + dir + "/*.java ";

            for (int k = 0; k < javaF.length; k++) {
                temp = javaF[k].getAbsolutePath();
                temp.replace('\\', '/');
                targetFiles.add(temp.substring(MutationSystem.SRC_PATH.length() + 1, temp.length()));
            }
        }

        File[] sub_dir = dirF.listFiles(new DirFileFilter());
        if (sub_dir.length == 0) return result;

        for (int i = 0; i < sub_dir.length; i++) {
            result = getJavacArgForDir(sub_dir[i].getAbsolutePath(), result, targetFiles);
        }
        return result;
    }

    /**
     * Return list of class names = class name of <i>dir</i> directory + <i>result</i>
     */
    public static String[] getAllClassNames(String[] result, String dir) {
        String[] classes;
        String temp;
        File dirF = new File(dir);

        File[] classF = dirF.listFiles(new ExtensionFilter("java"));
        if (classF != null) {
            classes = new String[classF.length];
            for (int k = 0; k < classF.length; k++) {
                temp = classF[k].getAbsolutePath();
                classes[k] = temp.substring(MutationSystem.SRC_PATH.length()+1, temp.length() - ".java".length());
                classes[k] = classes[k].replace('\\', '.');
                classes[k] = classes[k].replace('/', '.');
                //System.out.println(classes[k]);
            }
            result = addClassNames(result, classes);
        }

        File[] sub_dir = dirF.listFiles(new DirFileFilter());
        if (sub_dir == null) return result;

        for (int i = 0; i < sub_dir.length; i++) {
            result = getAllClassNames(result, sub_dir[i].getAbsolutePath());
        }
        return result;
    }


    /**
     * Return combine list of <i> list1 </i> and <i> list2</i> lists.
     *
     * @param list1 String list
     * @param list2 String list
     * @return combined list of list1 and list2
     */
    private static final String[] addClassNames(String[] list1, String[] list2) {
        if (list1 == null)
            list1 = list2;
        else {
            int num = list1.length;
            String[] temp = new String[num];

            for (int i = 0; i < temp.length; i++) {
                temp[i] = list1[i];
            }

            num = num + list2.length;
            list1 = new String[num];

            for (int i = 0; i < temp.length; i++) {
                list1[i] = temp[i];
            }

            for (int i = temp.length; i < num; i++) {
                list1[i] = list2[i - temp.length];
            }
        }
        return list1;
    }

    /**
     * Return inheritance information for give class <br>
     *
     * @param class_name name of class
     * @return inheritance information
     */
    public static InheritanceINFO getInheritanceInfo(String class_name) {
        for (int i = 0; i < classInfo.length; i++) {
            if (classInfo[i].getClassName().equals(class_name))
                return classInfo[i];
        }
        return null;
    }

    /**
     * Recognize class relation(extends and implements) of all classes located at CLASS_PATH directory. <br>
     * <b>* CAUTION: </b> this function should be called before
     * using <i>MutantsGenerator</i> or sub-classes of <i>MutantsGenerator</i>.
     */
    public static void recordClassRelation(){
        String[] classes = null;
        classes = MutationSystem.getAllClassNames(classes, MutationSystem.SRC_PATH);

        if (classes == null) {
            System.err.println("[WARN] There are no classes to mutate.");
            System.err.println(" Please check the directory  " + MutationSystem.SRC_PATH + " and be sure that src_path is set correctly (without a trailing slash) in mutator.xml.");
            //Runtime.getRuntime().exit(0);
            return;
        }
        classInfo = new InheritanceINFO[classes.length];
        ImplementRelation implRelation = ImplementRelation.getInstance();
        boolean[] bad = new boolean[classes.length];
        //add the class path dynamically
        addURL();
        for (int i = 0; i < classes.length; i++) {
            bad[i] = false;
            try {
                //create a new class from the class name
                Class c = Class.forName(classes[i]);
                //create the parent class of the class above
                Class parent = c.getSuperclass();
                String parentName = "";
                if ((parent == null) || (parent.getName().equals("java.lang.Object"))) {
                    if (c.isInterface() && c.getInterfaces().length >= 1) {
                        parentName = c.getInterfaces()[0].getName();
                    }
                } else {
                    parentName = parent.getName();
                }
                classInfo[i] = new InheritanceINFO(classes[i], parentName);
                // add class' implements relation
                ImplementINFO implInfo = new ImplementINFO(classes[i], c.getInterfaces());
                implRelation.addImplementINFO(classes[i], implInfo);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("[ERROR] Can't find the class: " + classes[i]);
                System.err.println("Please check that the compiled class for the code you want to mutate is in the classes/ directory. Also check that the classes_path variable in mutator.xml does not end with a trailing slash. ");
                bad[i] = true;
                classInfo[i] = new InheritanceINFO(classes[i], "");
                Runtime.getRuntime().exit(0);
            } catch (Error er) {
                // Sometimes error occurred. However, I can't solve..
                // To users: try do your best to solve it. ^^;
                logger.warn("Parsing class "+ classes[i]+" failed, lie in the method RecordClassRelation() of the MutationSystem class");
                //System.out.println("Parsing class "+ classes[i]+" fails in the method RecordClassRelation() of the MutationSystem class");
                bad[i] = true;
                classInfo[i] = new InheritanceINFO(classes[i], "");
            }
        }

        for (int i = 0; i < classes.length; i++) {
            if (bad[i])
                continue;

            String parent_name = classInfo[i].getParentName();

            for (int j = 0; j < classes.length; j++) {
                if (bad[j])
                    continue;

                if (classInfo[j].getClassName().equals(parent_name)) {
                    classInfo[i].setParent(classInfo[j]);
                    classInfo[j].addChild(classInfo[i]);
                    break;
                }
            }
        }
    }

    /**
     * add the class path dynamically
     *
     */
    private static void addURL() {
        try{
            Method addClass = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            addClass.setAccessible(true);
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            // load application based dependence jars
            File[] jarFiles = new File(MutationSystem.LIB_PATH).listFiles();
            for (File ff : jarFiles) {
                addClass.invoke(cl, new Object[]{ff.toURL()});
            }
            // load app's bytecode files
            File classDir = new File(CLASS_PATH);
            if(classDir.listFiles().length > 0){
                addClass.invoke(cl,new Object[]{classDir.toURL()});
            }
        }catch (Exception e){
            System.err.println(e.toString());
            logger.warn(e.toString());
        }
    }

    /**
     * Re-setting MJava structure for give class name <br>
     */
    public static void setJMutationPaths(String whole_class_name) {
        MutationSystem.CLASS_NAME = whole_class_name;
        MutationSystem.DIR_NAME = whole_class_name;
        MutationSystem.ORIGINAL_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.ORIGINAL_DIR_NAME;
        MutationSystem.TRADITIONAL_MUTANT_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.TM_DIR_NAME;
        MutationSystem.EXCEPTION_MUTANT_PATH = MutationSystem.MUTANT_HOME
                + "/" + whole_class_name + "/" + MutationSystem.EM_DIR_NAME;
    }

    /**
     * @deprecated <b> Default mjava system structure setting function </b>
     * <p> Recognize file structure for mutation system based on "mjava.config". </p>
     * <p> ** CAUTION : this function or `setJMutationStructure(String home_path)' should be called before generating and running mutants.
     */
    public static void setJMutationStructure()  //old version
    {
        try {
            File f = new File(MutationSystem.SYSTEM_HOME + "/mutator.config");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            String home_path = str.substring("Mutator_HOME=".length(), str.length());
            SRC_PATH = home_path + File.separator + "src";
            CLASS_PATH = home_path + File.separator + "classes";
            LIB_PATH = SYSTEM_HOME + File.separator + "libs";
            MUTANT_HOME = home_path + File.separator + "result";
        } catch (FileNotFoundException e1) {
            System.err.println("[ERROR] Can't find mutator.config file");
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated <p> Recognize file structure for mutation system from not "mutator.config" but from user directly </p>
     */
    public static void setJMutationStructure(String home_path) {
        SRC_PATH = home_path + File.separator + "src";
        LIB_PATH = SYSTEM_HOME + File.separator + "libs";
        CLASS_PATH = home_path + File.separator + "classes";
        MUTANT_HOME = home_path + File.separator + "result";
    }

    /**
     * @deprecated Lin add for setting sessions
     */
    public static void setJMutationStructureAndSession(String sessionName) {
        try {
            File f = new File(MutationSystem.SYSTEM_HOME + "/mutator.config");
            FileReader r = new FileReader(f);
            BufferedReader reader = new BufferedReader(r);
            String str = reader.readLine();
            String home_path = str.substring("Mutator_HOME=".length(), str.length());
            home_path = home_path + File.separator + sessionName;
            SRC_PATH = home_path + File.separator + "src";
            LIB_PATH = SYSTEM_HOME + File.separator + "libs";
            CLASS_PATH = home_path + File.separator + "classes";
            MUTANT_HOME = home_path + File.separator + "result";
        } catch (FileNotFoundException e1) {
            System.err.println("[ERROR] Can't find mutator.config file");
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * new read config method
     */
    public static void setMutationStructure() {
        try {
            if(GenMutantsMain.optionMap.containsKey("location_config")){
                XMLHandler.readLocationSettings(GenMutantsMain.optionMap.get("location_config"));
            }else {
                XMLHandler.readLocationSettings();
            }
            Map<String, String> settingsMap;
            if(GenMutantsMain.optionMap.containsKey("project_config")){
                settingsMap = new XMLHandler().readSettings(GenMutantsMain.optionMap.get("project_config"));
            }else {
                settingsMap = new XMLHandler().readSettings();
            }
            String home_path = settingsMap.get("mutation_home");
            File homeDir = new File(home_path);
            if (!homeDir.isDirectory()) {
                homeDir.mkdirs();
            }
            SRC_PATH = settingsMap.get("package_path");      //  XXX/src
            File srcDir = new File(SRC_PATH);
            if (!srcDir.isDirectory()) {
                srcDir.mkdirs();
            }
            LIB_PATH = SYSTEM_HOME + File.separator + "libs";
            if(settingsMap.containsKey("classes_path")) {
                CLASS_PATH = settingsMap.get("classes_path");
            }
            File classesDir = new File(CLASS_PATH);   //   XXX/classes
            if (!classesDir.isDirectory()) {
                classesDir.mkdirs();
            }
            MUTANT_HOME = settingsMap.get("mutants_path");   //  XXX/result
            File resultDir = new File(MUTANT_HOME);
            if (!resultDir.isDirectory()) {
                resultDir.mkdirs();
            }
            if (settingsMap.containsKey("python")) {
                PYTHON_PATH = settingsMap.get("python");
            }
            if (settingsMap.containsKey("MutationMethod")) {
                MUTATION_METHOD = settingsMap.get("MutationMethod");
            }
            String[] operators;
            if(GenMutantsMain.optionMap.containsKey("operators_config")){
                operators = ConfigHandler.readOperatorsConfig(GenMutantsMain.optionMap.get("operators_config"));
            }else {
                operators = ConfigHandler.readOperatorsConfig();
            }
            if(operators != null){
                tm_operators = operators;
            }
        } catch (SecurityException se) {
            System.err.println("Create directory failed !!!");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}