package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.GenericStmtWriter;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * LengthyGUIListener (cited from MDroid+)
 * Description:
 * Insert a long delay (\ie Thread.sleep(..)) in a GUI Listener
 * Code Example:
 * Before
 *  private View.OnClickListener listener = new View.OnClickListener() {
 * *    @Override
 *     public void onClick(View view) {
 *       clicksCount += 1;
 *     }
 * After
 *  private View.OnClickListener listener = new View.OnClickListener() {
 * *    @Override
 *     public void onClick(View view) {
 *       clicksCount += 1;
 * 		try {
 * 			Thread.sleep(10000);
 * 		} catch (InterruptedException e) {
 * 			e.printStackTrace();
 * 		}
 * }
 */
public class LengthyGUIListener extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(LengthyGUIListener.class);
    private boolean flag = false;
    public LengthyGUIListener(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {

        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(VariableDeclarator varDclr, Object obj){
                if("View.OnClickListener".equals(varDclr.getTypeAsString())){
                    flag = true;
                    super.visit(varDclr,obj);
                    flag = false;
                }
            }

            public void visit(ObjectCreationExpr objectCreationExpr, Object obj){
                if("View.OnClickListener".equals(objectCreationExpr.getTypeAsString())){
                    flag = true;
                    super.visit(objectCreationExpr,obj);
                    flag = false;
                }
            }

            public void visit(MethodDeclaration methodDclr,Object obj){
                super.visit(methodDclr,obj);
                if(!flag) return;
                String methodName = methodDclr.getNameAsString();
                if("onClick".equals(methodName) && methodDclr.getBody().isPresent()){
                    BlockStmt blockStmt  = methodDclr.getBody().get();
                    BlockStmt mutant = blockStmt.clone();
                    mutant.addStatement("try { Thread.sleep(10000);} catch (InterruptedException e) { e.printStackTrace();}");
                    outputToFile(blockStmt,mutant);
                }
            }
        },null);
    }

    /**
     * Output LengthyGUIListener mutants to files
     * @param original
     * @param mutant
     */
    private void outputToFile(BlockStmt original, BlockStmt mutant){
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("LengthyGUIListener");
        String mutant_dir = getMuantID("LengthyGUIListener");
        try{
            PrintWriter out = getPrintWriter(f_name);
            GenericStmtWriter writer = new GenericStmtWriter(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }catch (IOException ioe){
            System.err.println("LengthyGUIListener: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }

}
