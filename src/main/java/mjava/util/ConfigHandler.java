package mjava.util;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by  2018/12/22
 * @author Jian Liu
 */
public class ConfigHandler {
    static private  String settingsPath = System.getProperty("user.dir") + File.separator + "operators.config";

    public static String[] readOperatorsConfig(){
        return readOperatorsConfig(settingsPath);
    }

    public static String[] readOperatorsConfig(String filePath){
        String[] operators = null;
        try{
            File configFile = new File(filePath);
            if (configFile.exists() && configFile.isFile()){
                BufferedReader bfReader = new BufferedReader(new FileReader(configFile));
                String line;
                ArrayList<String> lines = Lists.newArrayList();
                while ((line = bfReader.readLine()) != null){
                    if (line.trim().startsWith("#") || line.trim().equals("")){
                        continue;
                    }
                    lines.add(line.trim());
                }
                operators = new String[lines.size()];
                int i = 0;
                for (String op : lines){
                    operators[i] = op;
                    i++;
                }
                bfReader.close();
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return operators;
    }

    static public void main(String[] args){
        //testing
        String[] operators =  readOperatorsConfig();
        if(operators != null){
            System.out.println("Size: "+operators.length);
            for (String value : operators)
            System.out.println(value);
        }
    }
}
