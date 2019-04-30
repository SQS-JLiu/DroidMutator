package edu.ecnu.sqslab.mjava;

import com.google.common.collect.Maps;
import mjava.model.ImplementINFO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * User Class Implements relation
 * Created by user on 2018/12/1.
 * @author Jian Liu
 */
public class ImplementRelation {

    static private ImplementRelation instance = null;
    // e.g.  key: edu.ecnu.sqslab.myThread   value:  ImplementINFO
    private HashMap<String,ImplementINFO> implementMaps = Maps.newHashMap();

    private ImplementRelation(){
    }

    static public ImplementRelation getInstance(){
        if(instance == null){
            synchronized (ImplementRelation.class){
                if(instance == null){
                    instance = new ImplementRelation();
                }
            }
        }
        return instance;
    }

    public void addImplementINFO(String key, ImplementINFO value){
        implementMaps.put(key,value);
    }

    public void removeImplementINFO(String key){
        implementMaps.remove(key);
    }

    public ImplementINFO getImplementINFO(String className){ //parameter format: java.lang.Thread
        if(implementMaps.containsKey(className)){
            return implementMaps.get(className);
        }
        return null;
    }

    public Set<String> getInterfaces(String className){ //parameter format: java.lang.Thread
        if(implementMaps.containsKey(className)){
            return implementMaps.get(className).getInterfaces();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer bf = new StringBuffer();
        Set<String> sets = implementMaps.keySet();
        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()){
            bf.append(implementMaps.get(iterator.next()).toString()+System.lineSeparator());
        }
        return bf.toString();
    }
}
