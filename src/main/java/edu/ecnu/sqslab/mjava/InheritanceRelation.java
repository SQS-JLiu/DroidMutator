package edu.ecnu.sqslab.mjava;

import com.google.common.collect.Maps;
import mjava.model.InheritanceINFO;

import java.util.HashMap;

/**
 * User Class Inheritance Relation
 * Created by user on 2018/11/30.
 * @author Jian Liu
 */
public class InheritanceRelation {
    static private InheritanceRelation instance = null;
    // e.g.  key: edu.ecnu.sqslab.myThread   value:  InheritanceINFO
    private HashMap<String,InheritanceINFO> inheritMaps = Maps.newHashMap();

    private InheritanceRelation(){
    }

    static public InheritanceRelation getInstance(){
        if(instance == null){
            synchronized (InheritanceRelation.class){
                if(instance == null){
                    instance = new InheritanceRelation();
                }
            }
        }
        return instance;
    }

    public void initInhertionInfo(){
        if(MutationSystem.classInfo == null){
            return;
        }
        for (InheritanceINFO info: MutationSystem.classInfo){
            inheritMaps.put(info.getClassName(),info);
        }
    }

    public void addInheritInfo(String key, InheritanceINFO value){
        inheritMaps.put(key,value);
    }

    public void removeInheritInfo(String key){
        inheritMaps.remove(key);
    }

    public InheritanceINFO getInheritInfo(String className){ // format: java.lang.Thread
        if(inheritMaps.containsKey(className)){
            return inheritMaps.get(className);
        }
        return null;
    }

    public String getSuperClassName(String className){ // format: java.lang.Thread
        if(inheritMaps.containsKey(className)){
            return inheritMaps.get(className).getParentName();
        }
        return "";
    }
}
