package mjava.op.record;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Sets;
import edu.ecnu.sqslab.mjava.MutationSystem;
import mjava.util.XMLHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by user on 2018/5/5.
 *
 * @author Jian Liu
 */
public abstract class MethodLevelMutator extends Mutator {
    //final String ClassTag = "MethodLevelMutator";
    protected String currentMethodSignature = null;
    //Marked statement line number set
    private Set<Integer> markStmtsSet = Sets.newHashSet();  //has control dependence
    protected File original_file;

    public MethodLevelMutator(CompilationUnit compileUnit, File originalFile) {
        super(compileUnit);
        this.original_file = originalFile;
    }

    /**
     * Retrieve the source's file name
     */
    public String getSourceName(String op_name) {
        // make directory for the mutant
        String dir_name = MutationSystem.MUTANT_PATH + "/" + currentMethodSignature + "/" + op_name + "_" + this.num;
        File f = new File(dir_name);
        f.mkdir();

        // return file name
        String name;
        name = dir_name + "/" + original_file.getName();
        return name;
    }

    /**
     * Return an ID of a given new_op name
     *
     * @param op_name
     * @return
     */
    public String getMuantID(String op_name) {
        String str = op_name + "_" + this.num;
        return str;
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException {
        File outfile = new File(f_name);
        FileWriter fout = new FileWriter(outfile);
        PrintWriter out = new PrintWriter(fout);
        return out;
    }

    protected abstract void generateMutants(MethodDeclaration p);

    /**
     * Handles classes that inherit or implement methods
     *
     * @param cid
     * @param obj
     */
    public void visit(ClassOrInterfaceDeclaration cid, Object obj) {
        Set<String> classTypeSets = classDeclarationType(cid);
        if (classTypeSets.size() > 0 || XMLHandler.dclrClassMaps.size() == 0) {
            cid.accept(new VoidVisitorAdapter<Set<String>>() {
                @Override
                public void visit(MethodDeclaration p, Set<String> typeSets) {
                    if (isMethodDeclaration(p, typeSets, XMLHandler.dclrClassMaps)) {
                        //System.out.println("========"+p.getNameAsString());
                        currentMethodSignature = getMethodSignature(p);
                        markStmtWithControlDep(p);
                        generateMutants(p);
                    } else {
                        currentMethodSignature = null;
                    }
                    super.visit(p, typeSets);
                }
            }, classTypeSets);
        }
        super.visit(cid, obj);
    }

    /**
     * Handling anonymous classes
     *
     * @param oce
     * @param obj
     */
    public void visit(ObjectCreationExpr oce, Object obj) {
        Set<String> typeSet = new HashSet<>();
        Set<String> nameSets = XMLHandler.objClassMaps.keySet();
        String anonymous_class = oce.getType().getNameAsString();
        if (nameSets.contains(anonymous_class)) {
            typeSet.add(anonymous_class);
        }
        if (typeSet.size() > 0 || XMLHandler.objClassMaps.size() == 0) {
            oce.accept(new VoidVisitorAdapter<Set<String>>() {
                @Override
                public void visit(MethodDeclaration p, Set<String> typeSet) {
                    if (isMethodDeclaration(p, typeSet, XMLHandler.objClassMaps)) {
                        currentMethodSignature = getMethodSignature(p);
                        markStmtWithControlDep(p);
                        generateMutants(p);
                    } else {
                        currentMethodSignature = null;
                    }
                    super.visit(p, typeSet);
                }
            }, typeSet);
        }
        super.visit(oce, obj);
    }

    /**
     * Skip this node and don't mutate it (Depends on the control_dependence item in muLocation.xml)
     * @param node
     * @return
     */
    public boolean skipMutation(Node node) {
        Integer lineNo = node.getBegin().get().line;
        // has control dependence
        if (ACTIVE_IDENTIFIER.equals(XMLHandler.control_dependence) && markStmtsSet.contains(lineNo)) {
            return true;
        }
        return false;
    }

    /**
     * mark statements that has control dependence
     *
     * @param cu
     */
    public void markStmtWithControlDep(MethodDeclaration cu) {
        cu.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(IfStmt ifStmt, Object arg) {
                super.visit(ifStmt, arg);
                List<Node> thenNodeList = ifStmt.getThenStmt().getChildNodes();
                for (Node node : thenNodeList) {
                    if (markStmtsSet.contains(node.getBegin().get().line)) {
                        continue;
                    }
                    markStmtsSet.add(node.getBegin().get().line);
                }
                if (ifStmt.hasElseBranch()) {
                    List<Node> elseNodeList = ifStmt.getElseStmt().get().getChildNodes();
                    for (Node node : elseNodeList) {
                        if (markStmtsSet.contains(node.getBegin().get().line)) {
                            continue;
                        }
                        markStmtsSet.add(node.getBegin().get().line);
                    }
                }
            }

            @Override
            public void visit(WhileStmt whileStmt, Object arg) {
                super.visit(whileStmt, arg);
                List<Node> nodeList = whileStmt.getBody().getChildNodes();
                for (Node node : nodeList) {
                    if (markStmtsSet.contains(node.getBegin().get().line)) {
                        continue;
                    }
                    markStmtsSet.add(node.getBegin().get().line);
                }
            }

            @Override
            public void visit(ForStmt forStmt, Object arg) {
                super.visit(forStmt, arg);
                List<Node> nodeList = forStmt.getBody().getChildNodes();
                for (Node node : nodeList) {
                    if (markStmtsSet.contains(node.getBegin().get().line)) {
                        continue;
                    }
                    markStmtsSet.add(node.getBegin().get().line);
                }
            }

            @Override
            public void visit(ForeachStmt foreachStmt, Object arg) {
                super.visit(foreachStmt, arg);
                List<Node> nodeList = foreachStmt.getBody().getChildNodes();
                for (Node node : nodeList) {
                    if (markStmtsSet.contains(node.getBegin().get().line)) {
                        continue;
                    }
                    markStmtsSet.add(node.getBegin().get().line);
                }
            }

        }, null);
    }

    public boolean isContainSpecificMethod(String body) {
        //skip method call
        if (body.contains("System.out.print") || body.contains("Log.")
                || body.contains("System.err.print") || body.contains("Toast.makeText")) {
            return true;
        }
        return false;
    }
}
