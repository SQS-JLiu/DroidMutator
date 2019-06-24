package mjava.op.apply_op;

import com.github.javaparser.ast.CompilationUnit;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.new_op.*;
import mjava.op.record.MethodLevelMutator;

import java.io.File;

/**
 * Applying a new extended mutation operator.
 */
public class ApplyNewOp {

    public static void newOperators(String[] traditionalOp,CompilationUnit comp_unit, File original_file){
        MethodLevelMutator mutant_op;
        if (MutantsGenerator.hasOperator(traditionalOp, "SAR")) {
            System.out.println("  Applying SAR ... ... ");
            mutant_op = new SAR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SAR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "SCR")) {
            System.out.println("  Applying SCR ... ... ");
            mutant_op = new SCR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("SCR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "CER")) {
            System.out.println("  Applying CER ... ... ");
            mutant_op = new CER(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("CER are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "FLR")) {
            System.out.println("  Applying FLR ... ... ");
            mutant_op = new FLR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("FLR are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "UOR")) {
            System.out.println("  Applying UOR ... ... ");
            mutant_op = new UOR(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("UOR are handled.");
        }
    }
}
