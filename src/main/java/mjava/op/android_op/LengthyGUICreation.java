package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
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
 * LengthyGUICreation (cited from MDroid+)
 * Description:
 * Insert a long delay (\ie Thread.sleep(..)) in the creation GUI thread
 * Code Example:
 * Before
 *      public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.main);
 *         Toast.makeText(getApplicationContext(),"2. onCreate()", Toast.LENGTH_SHORT).show();
 *     }
 * After
 *      public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *
 * 		try {
 * 			Thread.sleep(10000);
 *                } catch (InterruptedException e) {
 * 			e.printStackTrace();
 *        }
 *         setContentView(R.layout.main);
 *         Toast.makeText(getApplicationContext(),"2. onCreate()", Toast.LENGTH_SHORT).show();
 *     }
 */
public class LengthyGUICreation extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(LengthyGUICreation.class);
    public LengthyGUICreation(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(MethodDeclaration methodDclr,Object obj){
                String methodName = methodDclr.getNameAsString();
                String retType = methodDclr.getTypeAsString();
                if("onCreate".equals(methodName) && "void".equals(retType)){
                    if(methodDclr.getBody().isPresent()){
                        BlockStmt blockStmt  = methodDclr.getBody().get();
                        BlockStmt mutant = blockStmt.clone();
                        mutant.addStatement("try { Thread.sleep(10000);} catch (InterruptedException e) { e.printStackTrace();}");
                        outputToFile(blockStmt,mutant);
                    }
                }
                super.visit(methodDclr,obj);
            }
        },null);
    }

    /**
     * Output LengthyGUICreation mutants to files
     * @param original
     * @param mutant
     */
    private void outputToFile(BlockStmt original, BlockStmt mutant){
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("LengthyGUICreation");
        String mutant_dir = getMuantID("LengthyGUICreation");
        try{
            PrintWriter out = getPrintWriter(f_name);
            GenericStmtWriter writer = new GenericStmtWriter(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        }catch (IOException ioe){
            System.err.println("LengthyGUICreation: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
