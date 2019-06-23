package mjava.op.android_op;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import mjava.op.ExpressionWriter.MethodCallExpr_Writer;
import mjava.op.record.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * NullInputStream (cited from MDroid+)
 * Description:
 * Assign an input stream to null before it is closed
 * Detection Technique:
 * AST
 * Code Example:
 * Before
 * fileStream.close();
 * After
 * fileStream = null;
 * fileStream.close();
 */
public class NullInputStream extends MethodLevelMutator {
    private static final Logger logger = LoggerFactory.getLogger(NullInputStream.class);

    public NullInputStream(CompilationUnit comp_unit, File originalFile) {
        super(comp_unit, originalFile);
    }

    protected void generateMutants(MethodDeclaration p) {
        p.accept(new VoidVisitorAdapter<Object>() {
            public void visit(MethodCallExpr mc, Object obj) {
                super.visit(mc, obj);
                if (skipMutation(mc)) {
                    return;
                }
                if ("close".equals(mc.getNameAsString()) && mc.getScope().isPresent()) {
                    Expression callerExpr = mc.getScope().get();
                    //ResolvedType type = MutantsGenerator.getJavaParserFacade().getType(callerExpr);
                    //FileChannel.close,InputStream.close,BufferedInputStream.close,ByteArrayInputStream.close,
                    // DataInputStream.close,FilterInputStream.close,ObjectInputStream.close,PipedInputStream.close,
                    // SequenceInputStream.close,StringBufferInputStream.close,FileWriter.close,FileReader.close,
                    // BufferedInputStream.close ....too many
                    //if (type.describe().equals("xxx")) {
                        NameExpr mutant = new NameExpr(callerExpr.toString()+" = null; "+mc.toString());
                        outputToFile(mc,mutant);
                    //}
                }
            }
        }, null);
    }

    /**
     * Output NullInputStream mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, NameExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null) {
            return;
        }
        num++;
        String f_name = getSourceName("NullInputStream");
        String mutant_dir = getMuantID("NullInputStream");
        try {
            PrintWriter out = getPrintWriter(f_name);
            MethodCallExpr_Writer writer = new MethodCallExpr_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(original_file);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("NullInputStream: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
