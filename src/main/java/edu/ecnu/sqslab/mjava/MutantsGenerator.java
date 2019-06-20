package edu.ecnu.sqslab.mjava;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import mjava.gui.main.MutantsGenPanel;
import mjava.op.record.Mutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 *
 * @author Jian Liu
 */
public abstract class MutantsGenerator {
    private static Logger logger = LoggerFactory.getLogger(MutantsGenerator.class);

    protected File original_file;
    private String[] operators = null;
    CompilationUnit comp_unit = null;
    private static JavaParserFacade javaParserFacade = null;
    private static CombinedTypeSolver combinedTypeSolver;

    public MutantsGenerator(File f) {
        this.original_file = f;
    }

    public MutantsGenerator(File f, String[] operator_list) {
        this(f);
        operators = operator_list;
    }

    public void makeMutants() {
        //Generating parse tree.
        generateParseTree();
        if (comp_unit == null) {
            System.err.println(original_file + ", not a file, is Skipped !!!");
            logger.warn(original_file + ", not a file, is Skipped !!!");
            return;
        }
        String fileStr = comp_unit.toString();
        if (Mutator.isContainLocationKeyword(fileStr)) {
            MutantsGenPanel.setMutationSystemPathFor(original_file.getPath());
        } else {
            System.out.println("No target method," + original_file + " is Skipped !!!");
            logger.info("No target method," + original_file + " is Skipped !!!");
            return;
        }
        //Arranging original soure code.
        arrangeOriginal();
        //Generating Mutants
        genMutants();
    }

    /**
     * Arrange the original source file into an appropriate directory
     */
    private void arrangeOriginal() {
        if (comp_unit == null) {
            System.err.println(original_file + " is skipped.");
            return;
        }
        File outfile = null;
        try {
            InputStreamReader iReader = new InputStreamReader(new FileInputStream(original_file));
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = iReader.read()) != -1) {
                sb.append((char) c);
            }
            iReader.close();
            outfile = new File(MutationSystem.ORIGINAL_PATH, original_file.getName());
            FileWriter fout = new FileWriter(outfile);
            PrintWriter out = new PrintWriter(fout);
            out.println(sb.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("arrange Original: fails to create " + outfile);
        }
    }

    public void generateParseTree() {
        try {
            comp_unit = JavaParser.parse(new java.io.FileInputStream(original_file));
            //System.out.println(comp_unit.toString());
        } catch (java.io.FileNotFoundException e) {
            logger.error("File " + original_file + " not found." + e);
        }
    }

    abstract void genMutants();

    /**
     * @deprecated
     */
    public String getPublicClass(CompilationUnit comp_unit) {
        List<ClassOrInterfaceDeclaration> name = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration n : name) {
            if (n.isPublic()) {
                return n.getNameAsString();
            }
        }
        return null;
    }

    /**
     * Determine whether a string contain a certain new_op
     *
     * @param list
     * @param item
     * @return true if a string contain the new_op, false otherwise
     */
    public static boolean hasOperator(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item))
                return true;
        }
        return false;
    }

    public static void initTypeCheckingEnv() {
        try {
            logger.info("Initializing type checking environment. libs dir: "+MutationSystem.LIB_PATH);
            //Set up a minimal type solver  (Solver can not mutate android file)
            combinedTypeSolver = new CombinedTypeSolver();
            File fileList = new File(MutationSystem.LIB_PATH);
            fileList.listFiles();
            for (File file : fileList.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    combinedTypeSolver.add(new JarTypeSolver(file));
                    //System.out.println("Jar: "+file.getPath());
                }
            }
            JavaParserTypeSolver jTypeSolver = new JavaParserTypeSolver(MutationSystem.SRC_PATH);
            ReflectionTypeSolver rTypeSolver = new ReflectionTypeSolver();
            combinedTypeSolver.add(jTypeSolver);
            combinedTypeSolver.add(rTypeSolver);
            // Configure JavaParser to use type resolution
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
            JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
            JavaParser.getStaticConfiguration().setAttributeComments(false);
            javaParserFacade = JavaParserFacade.get(combinedTypeSolver);
        } catch (IOException ioe) {
            logger.error("Loading jar failed ." + ioe);
        }catch (Error error){
            logger.error("In initTypeCheckingEnv(): "+error.getMessage());
        }
    }

    public static JavaParserFacade getJavaParserFacade() {
        return javaParserFacade;
    }

    /**
     * Parsing node type
     * @param node
     * @return
     */
    public static String getNodeType(Node node){
        String type="";
        try {
            type = javaParserFacade.getType(node).describe();
        }catch (Exception ex){
            logger.info("Getting special node type failed, you can ignore this. Node is: "+node.toString());
        }catch (Error error){
            logger.warn("Getting node type failed, you can ignore this. Node is: "+node.toString());
        }
        return type;
    }

    /**
     * Parsing expression type (The type of local variable may not be detected)
     * @param expr
     * @return
     */
    public static String getNodeType(Expression expr){
        String type="";
        try {
            if(expr.isBinaryExpr() || expr.isAssignExpr() || expr.isLambdaExpr() || expr.isConditionalExpr()){
                return type;
            }
            type = javaParserFacade.getType(expr).describe();
        }catch (Exception ex){
            //Some classes may be declared in jar files.
            //You need to add the dependent jars to the libs directory of the DroidMutator.
            logger.info("Getting special node type failed.you can ignore this. Node is: "+expr.toString());
        }catch (Error error){
            //Some special nodes are difficult to parse ,for example:
            //protected static final String HECTOPASCAL = "hPa";
            //long pressure = Math.round(weatherInformation.getPressure());
            //String pressureInfo += pressure + " " + HECTOPASCAL;    (BinaryExpr: pressure+" "  +  HECTOPASCAL)
            //Parsing binary expression left node(failed) : pressure+" "      (Support for parsing a single node)
            logger.warn("Getting node type failed,you can ignore this. Node is: "+expr.toString());
        }
        return type;
    }

    /**
     * clear empty mutant directory
     */
    public boolean clearEmptyMutantsDir() {
        File file = new File(MutationSystem.MUTANT_HOME);
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                File tempFile = new File(f.getPath() + File.separator + MutationSystem.TM_DIR_NAME);
                if (tempFile.isDirectory()) {
                    for (File f2 : tempFile.listFiles()) {
                        if (f2.isDirectory() && f2.listFiles().length == 0) {
                            f2.delete();
                            String dirName = f2.getName();
                            // deleting method record in the method_list file
                            File method_list = new File(tempFile.getPath()+File.separator+"method_list");
                            List<String> lineList = new ArrayList<>();
                            String line;
                            try{
                                BufferedReader buffReader = new BufferedReader(new FileReader(method_list));
                                while ((line = buffReader.readLine()) != null){
                                    if(!dirName.equals(line)){
                                        lineList.add(line);
                                    }
                                }
                                buffReader.close();
                                BufferedWriter buffWriter = new BufferedWriter(new FileWriter(method_list));
                                for (String li : lineList){
                                    buffWriter.write(li+System.getProperty("line.separator"));
                                }
                                buffWriter.close();
                            }catch (IOException ioe){
                                ioe.printStackTrace();
                            }
                        }
                    }
                    File[] tempList = tempFile.listFiles();
                    if (tempList.length == 2) {
                        //delete all files and directories
                        delAllFile(f.getPath());
                    }
                }
            }
        }
        return true;
    }

    /**
     * 删除指定文件夹下的所有文件及子目录
     *
     * @param path
     * @return
     */
    public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if(file.isDirectory()){
            File[] tempList = file.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()) {
                    tempList[i].delete();
                }
                else if (tempList[i].isDirectory()) {
                    delAllFile(tempList[i].getPath());//删除文件夹里面的文件
                }
            }
        }
        file.delete();
    }

}
