package mjava.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.ecnu.sqslab.mjava.MutationSystem;
import mjava.op.record.Mutator;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by user on 2018/5/5.
 *
 * @author Jian Liu
 */
public class CreateDirForEachMethod extends Mutator {
    PrintWriter out = null;
    ClassOrInterfaceDeclaration cls;

    public CreateDirForEachMethod(ClassOrInterfaceDeclaration cls, PrintWriter out, CompilationUnit comp_unit) {
        super(comp_unit);
        this.out = out;
        this.cls = cls;
    }

    public void startMakeDir() {
        List<MethodDeclaration> methodList = cls.getMethods();
        for (MethodDeclaration m : methodList) {
            createDirectory(m);
        }
    }

    void createDirectory(String dir_name) {
        out.println(dir_name);
        String absolute_dir_path = MutationSystem.MUTANT_PATH + "/" + dir_name;
        File dirF = new File(absolute_dir_path);
        dirF.mkdir();
    }

    void createDirectory(MethodDeclaration m) {
        createDirectory(getMethodSignature(m));
    }

    void createDirectory(ConstructorDeclaration c) {
        createDirectory(getConstructorSignature(c));
    }

    public void visit(ConstructorDeclaration p, Object object) {
        super.visit(p, object);
    }

    public void visit(MethodDeclaration p, Object object) {
        if (isMethodDeclaration(p)) {
            String name = getMethodSignature(p);
            createDirectory(name);
        }
        super.visit(p, object);
    }
}
