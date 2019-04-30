package mjava.util;

import mjava.gui.dialog.BuildOutputPane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RuntimeExec  {
    public static Map<String, String> execute(String cmd) {
        int exitFlag = -1;
        StringBuffer rsltStrBuffer = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        Process process;
        BufferedReader br = null;
        try {
            process = runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                rsltStrBuffer.append(inline);
                System.out.println(inline);
                BuildOutputPane.getInstance().writePane(inline+"\n");
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((inline = br.readLine()) != null) {
                rsltStrBuffer.append(inline);
                System.out.println(inline);
                BuildOutputPane.getInstance().writePane(inline+"\n");
            }
            br.close();
            exitFlag = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> result = new HashMap<String, String>();
        result.put("exitFlag", String.valueOf(exitFlag));
        result.put("output", rsltStrBuffer.toString());
        return result;
    }
}
