package mjava.model;

import java.util.List;

public class MethodINFO {
    private String name;
    private String returnType;
    private List<String> modifiers;
    private List<String> parameters;

    public MethodINFO(){
    }

    public MethodINFO(String name,String returnType,List<String> modifiers,List<String> params){
        this.name = name;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.parameters = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
