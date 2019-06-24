package mjava.op.apply_op;

import com.github.javaparser.ast.CompilationUnit;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.java_op.*;
import mjava.op.java_op.COR;
import mjava.op.java_op.ROR;
import mjava.op.record.MethodLevelMutator;

import java.io.File;

/**
 * Apply Java-specific mutation operators
 */
public class ApplyJavaOp {
    public static void applyJavaOP(String[] traditionalOp, CompilationUnit comp_unit, File original_file){
        MethodLevelMutator mutant_op;
        if (MutantsGenerator.hasOperator(traditionalOp, "AODs")) {
            System.out.println("  Applying AODs ... ... ");
            mutant_op = new AODs(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AODs are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "AODu")) {
            System.out.println("  Applying AODu ... ... ");
            mutant_op = new AODu(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AODu are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "AORb")) {
            System.out.println("  Applying AORb ... ... ");
            mutant_op = new AORb(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORb are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "AORs")) {
            System.out.println("  Applying AORs ... ... ");
            mutant_op = new AORs(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORs are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "AORu")) {
            System.out.println("  Applying AORu ... ... ");
            mutant_op = new AORu(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AORu are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "ASR")) {
            System.out.println("  Applying ASR ... ... ");
            mutant_op = new ASR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("ASR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "LOR")) {
            System.out.println("  Applying LOR ... ... ");
            mutant_op = new LOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("LOR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "SOR")) {
            System.out.println("  Applying SOR ... ... ");
            mutant_op = new SOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SOR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "ROR")) {
            System.out.println("  Applying ROR ... ... ");
            mutant_op = new ROR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("ROR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "COR")) {
            System.out.println("  Applying COR ... ... ");
            mutant_op = new COR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("COR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "AOI")) {
            System.out.println("  Applying AOI ... ... ");
            mutant_op = new AOI(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("AOI are handled.");
        }
    }
}
