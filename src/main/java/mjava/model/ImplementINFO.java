package mjava.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User Class Implement Information
 * Created by user on 2018/12/1.
 * @author Jian Liu
 */
public class ImplementINFO {
    private String className;
    private Set<String> interfaces;

    public ImplementINFO(String className, Set<String> interfaces){
        this.className = className;
        this.interfaces = interfaces;
    }

    public ImplementINFO(String className, Class[] interfaces){
        this.className = className;
        this.interfaces = new HashSet<>();
        if(interfaces.length > 0){
            for (Class c : interfaces) {
                //android.os.Handler$Callback  ==> android.os.Handler.Callback
                this.interfaces.add(c.getName().replace("$","."));
            }
        }
    }

    public void addInterface(String interName){
        if(interfaces == null){
            interfaces = new HashSet<String>();
        }
        interfaces.add(interName);
    }

    public void removeInterface(String interName){
        if (interfaces != null){
            interfaces.remove(interName);
        }
    }

    public boolean isContainsInterface(String interName){
        if(interfaces == null){
            return false;
        }
        return interfaces.contains(interName);
    }

    public void setClassName(String className){
        this.className = className;
    }

    public void setInterfaces(Set<String> interfaces){
        this.interfaces = interfaces;
    }

    public String getClassName(){
        return className;
    }

    public Set<String> getInterfaces(){
        return interfaces;
    }

    @Override
    public String toString() {
        if(interfaces != null){
            StringBuffer sb = new StringBuffer();
            Iterator iterator = interfaces.iterator();
            while (iterator.hasNext()){
                sb.append(iterator.next());
            }
            return "Class Name: ["+className+"], Implements: ["+sb.toString()+"]";
        }
        return "Class Name: ["+className+"], Implements: ["+interfaces+"]";
    }
}
