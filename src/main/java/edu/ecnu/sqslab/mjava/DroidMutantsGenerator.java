package edu.ecnu.sqslab.mjava;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import mjava.op.apply_op.ApplyAndroidOp;
import mjava.op.apply_op.ApplyJavaOp;
import mjava.op.apply_op.ApplyNewOp;
import mjava.op.record.CodeChangeLog;
import mjava.op.record.CreateDirForEachMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 *
 * @author Jian Liu
 */
public class DroidMutantsGenerator extends MutantsGenerator {
    private final static Logger logger = LoggerFactory.getLogger(DroidMutantsGenerator.class);
    private String[] traditionalOp;

    public DroidMutantsGenerator(File f) {
        super(f);
        traditionalOp = MutationSystem.tm_operators;
    }

    public DroidMutantsGenerator(File f, String[] tOP) {
        super(f);
        traditionalOp = tOP;
    }

    void genMutants() {
        if (comp_unit == null) {
            logger.error("genMutants: AST is null. " + original_file + " is skipped.");
            System.out.println("genMutants: AST is null" + original_file + " is skipped.");
            return;
        }
        List<ClassOrInterfaceDeclaration> clsList = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        if (clsList == null || clsList.size() == 0) {
            return;
        }
        if (traditionalOp != null && traditionalOp.length > 0) {
            //Generating traditional mutants
            MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
            MutationSystem.clearPreviousTraditionalMutants();
            CodeChangeLog.openLogFile();
            //MultiThreadTriggerLog.clearList();
            genTraditionalMutants();
            //MultiThreadTriggerLog.writeAllLog(MutationSystem.CLASS_NAME);
            CodeChangeLog.closeLogFile();
            clearEmptyMutantsDir();
        }
    }

    void genTraditionalMutants() {
        List<ClassOrInterfaceDeclaration> clsList = comp_unit.getNodesByType(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration cls : clsList) {
            String tempName;
            try{
                tempName = cls.resolve().getQualifiedName();
            }catch (Exception e){
                tempName = cls.getNameAsString();
            }
            //take care of the case for generics
            if (tempName.indexOf("<") != -1 && tempName.indexOf(">") != -1) {
                tempName = tempName.substring(0, tempName.indexOf("<")) + tempName.substring(tempName.lastIndexOf(">") + 1, tempName.length());
            }
            //System.out.println("Class Name: "+tempName+ "     CLASS_NAME:"+MutationSystem.CLASS_NAME);
            if (tempName.equals(MutationSystem.CLASS_NAME) || tempName.endsWith("."+MutationSystem.CLASS_NAME)) {
                //generate a list of methods from the original java class
                try {
                    File f = new File(MutationSystem.MUTANT_PATH, "method_list");
                    FileOutputStream fout = new FileOutputStream(f);
                    PrintWriter out = new PrintWriter(fout, true);
                    CreateDirForEachMethod creatDir = new CreateDirForEachMethod(cls, out, comp_unit);
                    //creatDir.startMakeDir();
                    comp_unit.accept(creatDir, null);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException fnf) {
                    System.err.println("Error in writing method list.");
                    System.out.println(fnf.toString());
                    return;
                }
                ApplyNewOp.newOperators(traditionalOp,comp_unit,original_file);
                ApplyJavaOp.applyJavaOP(traditionalOp,comp_unit,original_file);
                ApplyAndroidOp.applyAndroidOP(traditionalOp,comp_unit,original_file);
            }
        }
    }
}
