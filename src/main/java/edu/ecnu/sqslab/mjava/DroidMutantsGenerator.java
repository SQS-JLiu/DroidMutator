package edu.ecnu.sqslab.mjava;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import mjava.op.android_op.*;
import mjava.op.basic_op.*;
import mjava.op.operator.*;
import mjava.op.record.CodeChangeLog;
import mjava.op.record.MethodLevelMutator;
import mjava.util.CreateDirForEachMethod;
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
                // Waiting for optimization using reflection approach
                myOperators();
                applyBasicOP();
                applyAndroidOP();
            }
        }
    }

    private void applyAndroidOP() {
        // Waiting for optimization using reflection approach
        MethodLevelMutator mutant_op;
        if (hasOperator(traditionalOp, "NullIntent")) {
            System.out.println("  Applying NullIntent ... ... ");
            mutant_op = new NullIntent(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullIntent are handled.");
        }
        if (hasOperator(traditionalOp, "NullLocation")) {
            System.out.println("  Applying NullLocation ... ... ");
            mutant_op = new NullLocation(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullLocation are handled.");
        }
        if (hasOperator(traditionalOp, "NullBackEndServiceReturn")) {
            System.out.println("  Applying NullBackEndServiceReturn ... ... ");
            mutant_op = new NullBackEndServiceReturn(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullBackEndServiceReturn are handled.");
        }
        if (hasOperator(traditionalOp, "NullBluetoothAdapter")) {
            System.out.println("  Applying NullBluetoothAdapter ... ... ");
            mutant_op = new NullBluetoothAdapter(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullBluetoothAdapter are handled.");
        }
        if (hasOperator(traditionalOp, "InvalidURI")) {
            System.out.println("  Applying InvalidURI ... ... ");
            mutant_op = new InvalidURI(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidURI are handled.");
        }
        if (hasOperator(traditionalOp, "InvalidIDFindView")) {
            System.out.println("  Applying InvalidIDFindView ... ... ");
            mutant_op = new InvalidIDFindView(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidIDFindView are handled.");
        }
        if (hasOperator(traditionalOp, "InvalidDate")) {
            System.out.println("  Applying InvalidDate ... ... ");
            mutant_op = new InvalidDate(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidDate are handled.");
        }
        if (hasOperator(traditionalOp, "InvalidFilePath")) {
            System.out.println("  Applying InvalidFilePath ... ... ");
            mutant_op = new InvalidFilePath(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidFilePath are handled.");
        }
        if (hasOperator(traditionalOp, "NullInputStream")) {
            System.out.println("  Applying NullInputStream ... ... ");
            mutant_op = new NullInputStream(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullInputStream are handled.");
        }
        if (hasOperator(traditionalOp, "RandomActionIntentDefinition")) {
            System.out.println("  Applying RandomActionIntentDefinition ... ... ");
            mutant_op = new RandomActionIntentDefinition(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("RandomActionIntentDefinition are handled.");
        }
        if (hasOperator(traditionalOp, "NewParamIntentPutExtras")) {
            System.out.println("  Applying NewParamIntentPutExtras ... ... ");
            mutant_op = new NewParamIntentPutExtras(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NewParamIntentPutExtras are handled.");
        }
        if (hasOperator(traditionalOp, "InvalidKeyIntentPutExtra")) {
            System.out.println("  Applying InvalidKeyIntentPutExtra ... ... ");
            mutant_op = new InvalidKeyIntentPutExtra(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidKeyIntentPutExtra are handled.");
        }
        if (hasOperator(traditionalOp, "FindViewByIdReturnNull")) {
            System.out.println("  Applying FindViewByIdReturnNull ... ... ");
            mutant_op = new FindViewByIdReturnNull(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("FindViewByIdReturnNull are handled.");
        }
        if (hasOperator(traditionalOp, "ViewComponentNotVisible")) {
            System.out.println("  Applying ViewComponentNotVisible ... ... ");
            mutant_op = new ViewComponentNotVisible(comp_unit,original_file);
            comp_unit.accept(mutant_op,null);
            System.out.println("ViewComponentNotVisible are handled.");
        }
    }

    private void applyBasicOP() {
        MethodLevelMutator mutant_op;
        if (hasOperator(traditionalOp, "AODs")) {
            System.out.println("  Applying AODs ... ... ");
            mutant_op = new AODs(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AODs are handled.");
        }
        if (hasOperator(traditionalOp, "AODu")) {
            System.out.println("  Applying AODu ... ... ");
            mutant_op = new AODu(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AODu are handled.");
        }
        if (hasOperator(traditionalOp, "AORb")) {
            System.out.println("  Applying AORb ... ... ");
            mutant_op = new AORb(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORb are handled.");
        }
        if (hasOperator(traditionalOp, "AORs")) {
            System.out.println("  Applying AORs ... ... ");
            mutant_op = new AORs(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORs are handled.");
        }
        if (hasOperator(traditionalOp, "AORu")) {
            System.out.println("  Applying AORu ... ... ");
            mutant_op = new AORu(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORu are handled.");
        }
        if (hasOperator(traditionalOp, "ASR")) {
            System.out.println("  Applying ASR ... ... ");
            mutant_op = new ASR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("ASR are handled.");
        }
        if (hasOperator(traditionalOp, "LOR")) {
            System.out.println("  Applying LOR ... ... ");
            mutant_op = new LOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("LOR are handled.");
        }
        if (hasOperator(traditionalOp, "SOR")) {
            System.out.println("  Applying SOR ... ... ");
            mutant_op = new SOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SOR are handled.");
        }
        if (hasOperator(traditionalOp, "ROR")) {
            System.out.println("  Applying ROR ... ... ");
            mutant_op = new ROR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("ROR are handled.");
        }
        if (hasOperator(traditionalOp, "COR")) {
            System.out.println("  Applying COR ... ... ");
            mutant_op = new COR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("COR are handled.");
        }
    }

    private void myOperators() {
        MethodLevelMutator mutant_op;
        if (hasOperator(traditionalOp, "SAR")) {
            System.out.println("  Applying SAR ... ... ");
            mutant_op = new SAR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SAR are handled.");
        }
        if (hasOperator(traditionalOp, "SCR")) {
            System.out.println("  Applying SCR ... ... ");
            mutant_op = new SCR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SCR are handled.");
        }
        if (hasOperator(traditionalOp, "CER")) {
            System.out.println("  Applying CER ... ... ");
            mutant_op = new CER(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("CER are handled.");
        }
        if (hasOperator(traditionalOp, "AVR")) {
            System.out.println("  Applying AVR ... ... ");
            mutant_op = new AVR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AVR are handled.");
        }
        if (hasOperator(traditionalOp, "FLR")) {
            System.out.println("  Applying FLR ... ... ");
            mutant_op = new FLR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("FLR are handled.");
        }
        if (hasOperator(traditionalOp, "UOR")) {
            System.out.println("  Applying UOR ... ... ");
            mutant_op = new UOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("UOR are handled.");
        }
    }
}
