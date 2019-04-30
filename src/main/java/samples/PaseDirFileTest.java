package samples;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Paths;
/**
 * Created by user on 2018/5/7.
 */
public class PaseDirFileTest {
    /**
     * Some code that uses JavaParser.
     */
        public static void main(String[] args) throws IOException {
            // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
            // In this case the root directory is found by taking the root from the current Maven module,
            // with src/main/resources appended.
            SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(PaseDirFileTest.class).resolve("src/main/resources"));

            // Our sample is in the root of this directory, so no package name.
            CompilationUnit cu = sourceRoot.parse("", "xxxx.java");

            cu.accept(new ModifierVisitor<Void>() {
                /**
                 * For every if-statement, see if it has a comparison using "!=".
                 * Change it to "==" and switch the "then" and "else" statements around.
                 */
                @Override
                public Visitable visit(IfStmt n, Void arg) {
                    // Figure out what to get and what to cast simply by looking at the AST in a debugger!
                    n.getCondition().ifBinaryExpr(binaryExpr -> {
                        if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        /* It's a good idea to clone nodes that you move around.
                            JavaParser (or you) might get confused about who their parent is!
                        */
                            Statement thenStmt = n.getThenStmt().clone();
                            Statement elseStmt = n.getElseStmt().get().clone();
                            n.setThenStmt(elseStmt);
                            n.setElseStmt(thenStmt);
                            binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                        }
                    });
                    return super.visit(n, arg);
                }
            }, null);

            // This saves all the files we just read to an output directory.
            sourceRoot.saveAll(
                    // The path of the Maven module/project which contains the LogicPositivizer class.
                    CodeGenerationUtils.mavenModuleRoot(PaseDirFileTest.class)
                            // appended with a path to "output"
                            .resolve(Paths.get("output")));
        }
}
