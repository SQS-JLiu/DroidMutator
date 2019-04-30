package samples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

/**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class ParseTypeTest {
    /**
     * Some code that uses JavaSymbolSolver.
     */
        public static void main(String[] args) {
            // Set up a minimal type solver that only looks at the classes used to run this sample.
            CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
            combinedTypeSolver.add(new ReflectionTypeSolver());

            // Configure JavaParser to use type resolution
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
            JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

            // Parse some code
            CompilationUnit cu = JavaParser.parse("class X { int x() { char a=5,c=0;" +
                    "String str=\"\";  str = \"123\"+\"123\";" + "a=c+a;return 1 + 1.0 - 5; } }");

            // Find all the calculations with two sides:
            cu.findAll(BinaryExpr.class).forEach(be -> {
                // Find out what type it has:
                ResolvedType resolvedType = be.calculateResolvedType();
                // Show that it's "double" in every case:
                System.out.println(be.toString() + " is a: " + resolvedType);
            });
        }
}
