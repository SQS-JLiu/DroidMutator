package mjava.op.record;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import edu.ecnu.sqslab.mjava.ImplementRelation;
import edu.ecnu.sqslab.mjava.InheritanceRelation;
import edu.ecnu.sqslab.mjava.MutationSystem;
import mjava.model.MethodINFO;
import mjava.util.XMLHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by user on 2018/5/8.
 *
 * @author Jian Liu
 */
public class Mutator extends VoidVisitorAdapter<Object> {
    public int num = 0;
    protected CompilationUnit comp_unit;
    public static final String ACTIVE_IDENTIFIER = "Y";

    public Mutator(CompilationUnit comp_unit) {
        this.comp_unit = comp_unit;
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException {
        File outfile = new File(f_name);
        FileWriter fout = new FileWriter(outfile);
        PrintWriter out = new PrintWriter(fout);
        return out;
    }

    /**
     * Return file name
     * @deprecated
     */
    public String getSourceName() {
        // make directory for the mutant
        String dir_name = MutationSystem.MUTANT_PATH + "/" + getClassName() + "_" + this.num;
        File f = new File(dir_name);
        f.mkdir();

        // return file name
        String name;
        name = dir_name + "/" + MutationSystem.CLASS_NAME + ".java";
        return name;
    }


    /**
     * Return a class name
     */
    public String getClassName() {
        Class cc = this.getClass();
        return exclude(cc.getName(), cc.getPackage().getName());
    }

    /**
     * Remove a portion of string from a specific position
     *
     * @param a
     * @param b
     * @return
     */
    public String exclude(String a, String b) {
        return a.substring(b.length() + 1, a.length());
    }

    /**
     * Return an ID of a mutant
     *
     * @return
     */
    public String getMuantID() {
        String str = getClassName() + "_" + this.num;
        return str;
    }

    public String getConstructorSignature(ConstructorDeclaration p) {
        String str = p.getName() + "(";
        NodeList<Parameter> pars = p.getParameters();
        str += getParameterString(pars);
        str += ")";
        return String.valueOf(p.getBegin().get().line) + "_" + str;
    }

    public String getMethodSignature(MethodDeclaration p) {
        //remover the generics in the return type
        String temp = p.getType().asString();
        if (temp.indexOf("<") != -1 && temp.indexOf(">") != -1) {
            temp = temp.substring(0, temp.indexOf("<")) + temp.substring(temp.lastIndexOf(">") + 1, temp.length());
        }
        String str = temp + "_" + p.getName() + "(";
        NodeList<Parameter> pars = p.getParameters();
        str += getParameterString(pars);
        str += ")";
        return String.valueOf(p.getBegin().get().line) + "_" + str;
    }

    String getParameterString(NodeList<Parameter> pars) {
        String str = "";
        //the for loop goes through each parameter of a method and return them in a String, separated by comma
        for (int i = 0; i < pars.size(); i++) {
            //because generics in introduced, the original code does not work anymore
            //the code below applies the cheapest solution: ignore generics by removing the contents between '<' and '>'
            String tempParameter = pars.get(i).getTypeAsString();
            if (tempParameter.indexOf("<") >= 0 && tempParameter.indexOf(">") >= 0) {
                tempParameter = tempParameter.substring(0, tempParameter.indexOf("<")) + tempParameter.substring(tempParameter.lastIndexOf(">") + 1, tempParameter.length());
                str += tempParameter;
            } else {
                str += tempParameter;
            }

            if (i != (pars.size() - 1))
                str += ",";
        }
        return str;
    }

    /**
     * Determines whether the file to be mutated has a location keyword
     * @param bodyStr
     * @return
     */
    public static boolean isContainLocationKeyword(String bodyStr) {
        if(XMLHandler.objClassMaps.size() == 0){
            return true;
        }
        Set<String> classNames = XMLHandler.objClassMaps.keySet();
        for (String className : classNames) {
            if (Pattern.compile(className).matcher(bodyStr).find()) {
                return true;
            }
            Collection<MethodINFO> mInfo = XMLHandler.objClassMaps.get(className);
            for (MethodINFO info : mInfo){
                if (Pattern.compile(info.getName()).matcher(bodyStr).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isMethodDeclaration(MethodDeclaration p) {
        return isMethodDeclaration(p,XMLHandler.dclrClassMaps.keySet(),XMLHandler.dclrClassMaps);
    }

    protected boolean isMethodDeclaration(MethodDeclaration p, Set<String> classTypeSet,Multimap<String,MethodINFO> maps) {
        if(maps.size() == 0){ // mutate all methods
            return true;
        }
        String name = p.getNameAsString();
        EnumSet<Modifier> modifiers = p.getModifiers();
        List<String> modifierList = Lists.newArrayList();
        for (Modifier m : modifiers){
            modifierList.add(m.asString());
        }
        NodeList<Parameter> params = p.getParameters();
        List<String> paramTypeList = Lists.newArrayList();
        for (Parameter param : params){
            paramTypeList.add(param.getTypeAsString());
        }
        String returnType = p.getTypeAsString();
        for(String className : classTypeSet){
            Collection<MethodINFO> mINFOs = maps.get(className);
            for (MethodINFO mInfo : mINFOs){
                if(name.equals(mInfo.getName()) &&
                        (returnType.equals(mInfo.getReturnType()) || "*".equals(mInfo.getReturnType())) &&
                        (modifierList.equals(mInfo.getModifiers())|| mInfo.getModifiers().contains("*")) &&
                        (paramTypeList.equals(mInfo.getParameters()) || mInfo.getParameters().contains("*"))){
                    return true;
                }
            }
        }
        return  false;
    }

    public Set<String> classDeclarationType(ClassOrInterfaceDeclaration cid) {
        Set<String> dclrClassNameSets = Sets.newHashSet();
        //Firstly, judge the current Class
        classNameJudge(dclrClassNameSets,cid.resolve().getQualifiedName());
        NodeList<ClassOrInterfaceType> extendList = cid.getExtendedTypes();
        NodeList<ClassOrInterfaceType> implementList = cid.getImplementedTypes();
        for (ClassOrInterfaceType c : extendList) {
            String name;
            try{
                name = c.resolve().getQualifiedName();
            }catch (Exception e){
                name = c.getNameAsString();
            }
            if(!classNameJudge(dclrClassNameSets,name)){
                //Multiple inheritance and implements problem
                multiExtAndImplSearch(dclrClassNameSets,name);
            }
        }
        for (ClassOrInterfaceType c : implementList) {
            String name;
            try{
                name = c.resolve().getQualifiedName();
            }catch (Exception e){
                name = c.getNameAsString();
            }
            if(!classNameJudge(dclrClassNameSets,name)){
                //Multiple implements problem
                multiExtendsSearch(dclrClassNameSets,name);  // Consider multiple inheritance of interfaces
            }
        }
        return dclrClassNameSets;
    }

    /**
     * Recursive judgment of multiple inheritance relationships
     * @param classNameSets
     * @param className
     */
    private void multiExtendsSearch(Set<String> classNameSets, String className) {
        String superName = InheritanceRelation.getInstance().getSuperClassName(className);
        if ("".equals(superName)) {
            // do nothings
        } else if (!classNameJudge(classNameSets,superName)) {
            multiExtendsSearch(classNameSets,superName);
        }
    }

    /**
     * Recursive judgment of multiple inheritance and multiple implementation judgment
     * @param classNameSets
     * @param className
     */
    private void multiExtAndImplSearch(Set<String> classNameSets, String className) {
        Set<String> clsNames = ImplementRelation.getInstance().getInterfaces(className);
        Set<String> nameSets = XMLHandler.dclrClassMaps.keySet();
        multiExtendsSearch(classNameSets,className);
        if(clsNames == null){
            //do nothings
        }else {
            for(String name : clsNames){  //Multiple implements problem
                if(nameSets.contains(name)){
                    classNameSets.add(name);
                }else {
                    multiExtendsSearch(classNameSets,name);
                }
            }
        }
    }

    private boolean classNameJudge(Set<String> classNameSets, String className){
        Set<String> nameSets = XMLHandler.dclrClassMaps.keySet();
        if (nameSets.contains(className)) {
            classNameSets.add(className);
            return true;
        }
        return false;
    }
}
