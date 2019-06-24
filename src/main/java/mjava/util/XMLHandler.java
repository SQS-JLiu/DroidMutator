package mjava.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import mjava.model.MethodINFO;
import mjava.op.record.Mutator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class XMLHandler {
    private static String settingsPath = System.getProperty("user.dir") + File.separator + "mutator.xml";
    private static String muLocationPath = System.getProperty("user.dir") + File.separator + "muLocation.xml";
    public static Multimap<String, MethodINFO> dclrClassMaps = ArrayListMultimap.create();
    public static Multimap<String, MethodINFO> objClassMaps = ArrayListMultimap.create();
    public static String control_dependence;

    private static Document loadXML(String filepath) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        File xmlFile = new File(filepath);
        if(xmlFile.exists()){
            //Get the document object, if the document has no nodes, it will throw an Exception and end early
            return saxReader.read(xmlFile);
        }
        System.err.println("Class XMLHandler: XML file "+filepath+" is not exists.");
        return null;
    }

    public Map<String, String> readSettings(String filePath){
        settingsPath = filePath;
        return  readSettings();
    }

    public Map<String, String> readSettings() {
        Map<String, String> settingsMap = Maps.newHashMap();
        try {
            //System.out.println(settingsPath);
            Document document = loadXML(settingsPath);
            Element root = document.getRootElement();
            Element mutationElm = root.element("Mutation");
            List<Element> elmList = mutationElm.elements();
            for (Element elm : elmList) {
                settingsMap.put(elm.attributeValue("name"), elm.getText());
                //System.out.println(elm.attributeValue("name")+ " : " + elm.getText());
            }
            Element builderElm = root.element("Builder");
            List<Element> buildList = builderElm.elements();
            for (Element elm : buildList) {
                settingsMap.put(elm.attributeValue("name"), elm.getText());
                //System.out.println(elm.attributeValue("name")+ " : " + elm.getText());
            }
            if (root.element("Python") != null) {
                settingsMap.put("python", root.element("Python").getText());
            }
            if (root.element("MutationMethod") != null) {
                settingsMap.put("MutationMethod", root.element("MutationMethod").getText());
            }
        } catch (DocumentException de) {
            de.printStackTrace();
            return null;
        }
        return settingsMap;
    }

    public boolean writeSettings(Map<String, String> settingsMap) {
        try {
            Document document = loadXML(settingsPath);
            Element root = document.getRootElement();
            Element mutationElm = root.element("Mutation");
            List<Element> elmList = mutationElm.elements();
            for (Element elm : elmList) {
                String name = elm.attributeValue("name");
                if (settingsMap.containsKey(name)) {
                    elm.setText(settingsMap.get(name));
                }
            }
            Element builderElm = root.element("Builder");
            List<Element> buildList = builderElm.elements();
            for (Element elm : buildList) {
                String name = elm.attributeValue("name");
                if (settingsMap.containsKey(name)) {
                    elm.setText(settingsMap.get(name));
                }
            }
            XMLWriter writer = new XMLWriter(new FileWriter(new File(settingsPath)));
            writer.write(document);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    static public void readLocationSettings() {
        readLocationSettings(muLocationPath);
    }

    static public void readLocationSettings(String filePath) {
        try {
            Document document = loadXML(filePath);
            Element root = document.getRootElement();
            control_dependence = root.attributeValue("control_dependence");
            String rootActive = root.attributeValue("active");
            if (!Mutator.ACTIVE_IDENTIFIER.equals(rootActive)) {
                return;
            }
            List<org.dom4j.Element> cElmList = root.elements("Class");
            String name, qualifiedName, active;
            for (Element elm : cElmList) {
                name = elm.attributeValue("name");
                qualifiedName = elm.attributeValue("qualifiedName");
                active = elm.attributeValue("active");
                if (Mutator.ACTIVE_IDENTIFIER.equals(active)) {
                    Element methodElm = elm.element("Methods");
                    List<Element> mElmList = methodElm.elements();
                    MethodINFO methodINFO;
                    String modifiers, params;
                    for (Element e : mElmList) {
                        methodINFO = new MethodINFO();
                        methodINFO.setName(e.attributeValue("name"));
                        modifiers = e.element("Modifiers").getText();
                        List<String> modLists = Lists.newArrayList();
                        if (!"".equals(modifiers) && modifiers != null) {
                            for (String str : modifiers.split(",")) {
                                modLists.add(str);
                            }
                        }
                        methodINFO.setModifiers(modLists);
                        methodINFO.setReturnType(e.element("returnType").getText());
                        params = e.element("Parameters").getText();
                        List<String> paramLists = Lists.newArrayList();
                        if (!"".equals(params) && params != null) {
                            for (String str : params.split(",")) {
                                paramLists.add(str);
                            }
                        }
                        methodINFO.setParameters(paramLists);
                        dclrClassMaps.put(qualifiedName, methodINFO);
                        objClassMaps.put(name, methodINFO);
                    }
                }
            }
        } catch (DocumentException de) {
            de.printStackTrace();
        }
    }

    public JSONObject readAndroidManifest(){
        Map<String,String> settingsMap = readSettings();
        JSONObject manifestJsonObj = new JSONObject();
        if(settingsMap.containsKey("AndroidManifest_path")){
            try{
                Document document = loadXML(settingsMap.get("AndroidManifest_path")+"/AndroidManifest.xml");
                Element root = document.getRootElement();
                String packageName = root.attributeValue("package");
                JSONArray permissionArray = new JSONArray();
                JSONObject activitiesObj = new JSONObject();
                manifestJsonObj.put("package",packageName);
                manifestJsonObj.put("user_permission",permissionArray);
                manifestJsonObj.put("activity",activitiesObj);
                List<org.dom4j.Element> permissionElems =  root.elements("uses-permission");
                for (Element ele : permissionElems){
                    permissionArray.put(ele.attributeValue("name"));
                }
                org.dom4j.Element applicationElem = root.element("application");
                List<org.dom4j.Element> activityElems = applicationElem.elements("activity");
                for (Element ele : activityElems){
                    Element intent_filterElem = ele.element("intent-filter");
                    JSONObject activityObj = new JSONObject();
                    activityObj.put("parentActivityName",ele.attributeValue("parentActivityName"));
                    if(intent_filterElem == null){
                        activitiesObj.put(ele.attributeValue("name"),activityObj);
                        continue;
                    }
                    Element actionElem = intent_filterElem.element("action");
                    Element categoryElem  = intent_filterElem.element("category");
                    activityObj.put("action",actionElem.attributeValue("name"));
                    activityObj.put("category",categoryElem.attributeValue("name"));
                    activitiesObj.put(ele.attributeValue("name"),activityObj);
                }

            }catch (DocumentException de){
                //de.printStackTrace();
            }
        }
        return manifestJsonObj;
    }

    public static void main(String[] args) {
        //testing
        JSONObject results = new XMLHandler().readAndroidManifest();
        System.out.println(results.toString());
    }
}
