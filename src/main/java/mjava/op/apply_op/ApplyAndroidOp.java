package mjava.op.apply_op;

import com.github.javaparser.ast.CompilationUnit;
import edu.ecnu.sqslab.mjava.MutantsGenerator;
import mjava.op.android_op.*;
import mjava.op.record.MethodLevelMutator;

import java.io.File;

public class ApplyAndroidOp {
    public static void applyAndroidOP(String[] traditionalOp, CompilationUnit comp_unit, File original_file){
        MethodLevelMutator mutant_op;
        if (MutantsGenerator.hasOperator(traditionalOp, "NullIntent")) {
            System.out.println("  Applying NullIntent ... ... ");
            mutant_op = new NullIntent(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullIntent are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "NullLocation")) {
            System.out.println("  Applying NullLocation ... ... ");
            mutant_op = new NullLocation(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullLocation are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "NullBackEndServiceReturn")) {
            System.out.println("  Applying NullBackEndServiceReturn ... ... ");
            mutant_op = new NullBackEndServiceReturn(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullBackEndServiceReturn are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "NullBluetoothAdapter")) {
            System.out.println("  Applying NullBluetoothAdapter ... ... ");
            mutant_op = new NullBluetoothAdapter(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullBluetoothAdapter are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "InvalidURI")) {
            System.out.println("  Applying InvalidURI ... ... ");
            mutant_op = new InvalidURI(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidURI are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "InvalidIDFindView")) {
            System.out.println("  Applying InvalidIDFindView ... ... ");
            mutant_op = new InvalidIDFindView(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidIDFindView are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "InvalidDate")) {
            System.out.println("  Applying InvalidDate ... ... ");
            mutant_op = new InvalidDate(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidDate are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "InvalidFilePath")) {
            System.out.println("  Applying InvalidFilePath ... ... ");
            mutant_op = new InvalidFilePath(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidFilePath are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "NullInputStream")) {
            System.out.println("  Applying NullInputStream ... ... ");
            mutant_op = new NullInputStream(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NullInputStream are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "RandomActionIntentDefinition")) {
            System.out.println("  Applying RandomActionIntentDefinition ... ... ");
            mutant_op = new RandomActionIntentDefinition(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("RandomActionIntentDefinition are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "NewParamIntentPutExtras")) {
            System.out.println("  Applying NewParamIntentPutExtras ... ... ");
            mutant_op = new NewParamIntentPutExtras(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("NewParamIntentPutExtras are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "InvalidKeyIntentPutExtra")) {
            System.out.println("  Applying InvalidKeyIntentPutExtra ... ... ");
            mutant_op = new InvalidKeyIntentPutExtra(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("InvalidKeyIntentPutExtra are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "FindViewByIdReturnNull")) {
            System.out.println("  Applying FindViewByIdReturnNull ... ... ");
            mutant_op = new FindViewByIdReturnNull(comp_unit, original_file);
            comp_unit.accept(mutant_op, null);
            System.out.println("FindViewByIdReturnNull are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "ViewComponentNotVisible")) {
            System.out.println("  Applying ViewComponentNotVisible ... ... ");
            mutant_op = new ViewComponentNotVisible(comp_unit,original_file);
            comp_unit.accept(mutant_op,null);
            System.out.println("ViewComponentNotVisible are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "BuggyGUIListener")) {
            System.out.println("  Applying BuggyGUIListener ... ... ");
            mutant_op = new BuggyGUIListener(comp_unit,original_file);
            comp_unit.accept(mutant_op,null);
            System.out.println("BuggyGUIListener are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "LengthyGUICreation")) {
            System.out.println("  Applying LengthyGUICreation ... ... ");
            mutant_op = new LengthyGUICreation(comp_unit,original_file);
            comp_unit.accept(mutant_op,null);
            System.out.println("LengthyGUICreation are handled.");
        }
        if (MutantsGenerator.hasOperator(traditionalOp, "LengthyGUIListener")) {
            System.out.println("  Applying LengthyGUIListener ... ... ");
            mutant_op = new LengthyGUIListener(comp_unit,original_file);
            comp_unit.accept(mutant_op,null);
            System.out.println("LengthyGUIListener are handled.");
        }
    }
}
